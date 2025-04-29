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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    if (metricReport.metricList().isEmpty()) {
      throw new EmptyMetricListException("No metrics found in report");
    }
    var metricReportEntity = fromDomain(metricReport);
    LOG.debug("Successfully mapped metric report to metric report entity: {}", metricReportEntity);

    String reportSql = "INSERT INTO metric_report VALUES (?::UUID, ?::JSON, ?, ?)";
    String metricSql = "INSERT INTO metric VALUES (?::UUID, ?, ?::UUID, ?, ?::JSONB)";
    var connection = dbClient.unwrap(Connection.class);
    try {
      structuredInsert(connection, reportSql, metricSql, metricReportEntity);
    } catch (Exception e) {
      throw new DatabaseInsertException("Failed to insert report: " + metricReportEntity, e);
    }
  }

  private void structuredInsert(
      Connection connection,
      String reportSql,
      String metricSql,
      MetricReportEntity metricReportEntity)
      throws SQLException {
    var repoID = RepositoryId.getScopedValue().value();
    try (var reportStmt = connection.prepareStatement(reportSql);
        var metricStmt = connection.prepareStatement(metricSql)) {
      connection.setAutoCommit(false);

      reportStmt.setObject(1, metricReportEntity.id());
      reportStmt.setString(2, metricReportEntity.data());
      reportStmt.setTimestamp(3, Timestamp.from(metricReportEntity.timestamp()));
      reportStmt.setString(4, repoID);

      metricStmt.setObject(3, metricReportEntity.id());
      LOG.debug("Saving {} metrics", metricReportEntity.metricList().size());

      for (MetricEntity entity : metricReportEntity.metricList()) {
        metricStmt.setObject(1, entity.id());
        metricStmt.setString(2, entity.name());
        metricStmt.setString(4, entity.type());
        metricStmt.setString(5, entity.values());
        metricStmt.addBatch();
      }
      var reportRows = reportStmt.executeUpdate();
      var metricRows = metricStmt.executeBatch();

      if (reportRows != 1
          || metricRows.length != metricReportEntity.metricList().size()
          || hasFailedRows(metricRows)) {
        throw new DatabaseInsertException(
            "Expected 1 report and %s metrics to update but was: %s report and %s metrics "
                .formatted(metricReportEntity.metricList().size(), reportRows, metricRows.length));
      }
      connection.commit();
      LOG.debug("Saved metric report {}", metricReportEntity);
    } catch (SQLException | DatabaseInsertException e) {
      connection.rollback();
      throw new DatabaseInsertException("Failed to insert report: " + metricReportEntity, e);
    } finally {
      connection.close();
      LOG.debug("Closed db connection");
    }
  }

  private boolean hasFailedRows(int[] metricRows) {
    return Arrays.stream(metricRows).anyMatch(rows -> rows != 1);
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

      return result.stream().map(MetricReportEntity::toDomain).toList();
    } catch (Exception e) {
      LOG.error(
          "Unable to get metrics between {} and {} because of {}", start, end, e.getMessage());
      throw new RuntimeException(e);
    }
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
