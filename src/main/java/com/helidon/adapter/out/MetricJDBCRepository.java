package com.helidon.adapter.out;

import static com.helidon.adapter.out.entity.MetricReportEntity.*;

import com.helidon.adapter.RepositoryId;
import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricReportEntity;
import com.helidon.application.domain.model.MetricReport;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
import com.helidon.exception.DatabaseInsertException;
import com.helidon.exception.EmptyMetricListException;
import io.helidon.dbclient.DbClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.StructuredTaskScope;

import io.helidon.dbclient.DbStatement;
import io.helidon.dbclient.DbStatements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricJDBCRepository implements ForPersistingMetrics, ForManagingStoredMetrics {
  public static final Logger LOG = LoggerFactory.getLogger(MetricJDBCRepository.class);
  private final DbClient dbClient;

  public MetricJDBCRepository(DbClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public void saveMetrics(MetricReport metricReport) {
    LOG.debug("Saving metric report {}", metricReport);

    if (metricReport.metricList().isEmpty()) {
      throw new EmptyMetricListException("No metrics found in report");
    }

    var repoID = RepositoryId.getScopedValue().value();
    var metricReportEntity = fromDomain(metricReport);

    structuredInsert();

    var tx = dbClient.transaction();
    try {
      var updatedRows =
          tx.createNamedInsert("insert-metric-report")
              .params(
                  metricReportEntity.id(),
                  metricReportEntity.data(),
                  Timestamp.from(metricReportEntity.timestamp()),
                  repoID)
              .execute();

      if (updatedRows != 1) {
        throw new DatabaseInsertException("Failed to insert report: " + metricReportEntity);
      }

      LOG.debug("Metric list size: {}", metricReportEntity.metricList().size());

      String sql = "INSERT INTO metric VALUES (?::UUID, ?, ?::UUID, ?, ?::JSONB)";
      try(var connection = dbClient.unwrap(Connection.class);
      var stmt = connection.prepareStatement(sql)) {
        connection.setAutoCommit(false);
        for (MetricEntity entity : metricReportEntity.metricList()) {
          stmt.setObject(1, entity.id());
          stmt.setString(2, entity.name());
          stmt.setObject(3, metricReportEntity.id());
          stmt.setString(4, entity.type());
          stmt.setString(5, entity.type());
          stmt.addBatch();
        }
        stmt.executeBatch();
      }catch (SQLException e){

      }

//      for (MetricEntity entity : metricReportEntity.metricList()) {
//        var updatedRow =
//            tx.createNamedInsert("insert-metric")
//                .params(
//                    entity.id(),
//                    entity.name(),
//                    metricReportEntity.id(),
//                    entity.type(),
//                    entity.values())
//                .execute();
//        if (updatedRow != 1) {
//          throw new DatabaseInsertException("Failed to insert entity: " + entity);
//        }

      tx.commit();
    } catch (DatabaseInsertException e) {
      LOG.error("Error inserting metric report: {}", metricReportEntity);
      tx.rollback();
      throw e;
    } catch (Exception e) {
      tx.rollback();
      throw new DatabaseInsertException("Exception when inserting", e);
    }
  }

  /**
   * Helidon dbclient does not seem to support batch updates.
   * We need to unwrap the connection from the dbClient and use the inherit SQL logic to batch the metrics that will be inserted.
   * This will end up with one commit from dbclient transaction and one commit from a connection transaction.
   * Since these two separate commits persist data, the possibility is that one might fail while the other succeeds.
   * To make this fail-safe, we need to run this in a structured concurrency.
   * It's all or nothing.
   */
  private void structuredInsert() {
    try(var structuredScope = new StructuredTaskScope.ShutdownOnFailure()){

    }
  }

  @Override
  public MetricReport get(String id) {
    return null;
  }

  @Override
  public List<MetricReport> getBetweenDates(Instant start, Instant end) {
    LOG.debug("Getting metrics between {} and {}", start, end);
    var repoId = RepositoryId.getScopedValue().value();

    Map<UUID, MetricReportEntity> resultMap = new HashMap<>();

    try {
      dbClient
          .execute()
          .createNamedQuery("get-between-dates")
          .params(repoId, Timestamp.from(start), Timestamp.from(end))
          .execute()
          .forEach(
              row -> {
                UUID metricsId = row.column("report_id").get(UUID.class);
                Timestamp createdAt = row.column("created_at").get(Timestamp.class);
                UUID metricId = row.column("metric_id").get(UUID.class);
                String name = row.column("name").get(String.class);
                String type = row.column("type").get(String.class);
                String values = row.column("values").get(String.class);

                MetricEntity metricEntity =
                    new MetricEntity(metricId, name, metricsId, type, values);

                resultMap
                    .computeIfAbsent(
                        metricsId,
                        ignore ->
                            new MetricReportEntity(
                                metricsId, "{}", createdAt.toInstant(), new ArrayList<>()))
                    .metricList()
                    .add(metricEntity);
              });

      List<MetricReportEntity> result = new ArrayList<>(resultMap.values());

      LOG.debug("Fetched : {}", result);
      return result.stream().map(MetricReportEntity::toDomain).toList();
    } catch (Exception e) {
      LOG.error(
          "Unable to get metrics between {} and {} because of {}", start, end, e.getMessage());
    }
    return List.of();
  }

  @Override
  public List<MetricReport> getRecentFromView() {
    LOG.debug("Getting metrics from view");
    var repoId = RepositoryId.getScopedValue().value();
    Map<UUID, MetricReportEntity> resultMap = new HashMap<>();

    try {
      dbClient
          .execute()
          .createNamedQuery("view-last-thirty-days")
          .params(repoId)
          .execute()
          .forEach(
              row -> {
                UUID metricsId = row.column("metric_report_id").get(UUID.class);
                Timestamp createdAt = row.column("created_at").get(Timestamp.class);
                UUID metricId = row.column("metric_id").get(UUID.class);
                String name = row.column("name").get(String.class);
                String type = row.column("type").get(String.class);
                String values = row.column("values").get(String.class);

                MetricEntity metricEntity =
                    new MetricEntity(metricId, name, metricsId, type, values);

                resultMap
                    .computeIfAbsent(
                        metricsId,
                        _ ->
                            new MetricReportEntity(
                                metricsId, "{}", createdAt.toInstant(), new ArrayList<>()))
                    .metricList()
                    .add(metricEntity);
              });

      List<MetricReportEntity> result = new ArrayList<>(resultMap.values());

      return result.stream().map(MetricReportEntity::toDomain).toList();
    } catch (Exception e) {
      LOG.error("Something went wrong", e);
    }
    return List.of();
  }
}
