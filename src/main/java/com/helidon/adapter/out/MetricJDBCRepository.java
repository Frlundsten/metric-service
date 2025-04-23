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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
      for (MetricEntity entity : metricReportEntity.metricList()) {
        var updatedRow =
            tx.createNamedInsert("insert-metric")
                .params(
                    entity.id(),
                    entity.name(),
                    metricReportEntity.id(),
                    entity.type(),
                    entity.values())
                .execute();
        if (updatedRow != 1) {
          throw new DatabaseInsertException("Failed to insert entity: " + entity);
        }
      }
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

  @Override
  public MetricReport get(String id) {
    return null;
  }

  @Override
  public List<MetricReport> getBetweenDates(Instant start, Instant end) {
    LOG.debug("Getting metrics between {} and {}", start, end);

    var tx = dbClient.transaction();
    Map<String, MetricReportEntity> resultMap = new HashMap<>();

    try {
      tx.createNamedQuery("get-between-dates")
          .params(Timestamp.from(start), Timestamp.from(end))
          .execute()
          .forEach(
              row -> {
                String metricsId = row.column("report_id").get(String.class);
                Timestamp createdAt = row.column("created_at").get(Timestamp.class);
                String metricId = row.column("metric_id").get(String.class);
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
      var domain = result.stream().map(MetricReportEntity::toDomain).toList();
      return domain;
    } catch (Exception e) {
      LOG.error(
          "Unable to get metrics between {} and {} because of {}", start, end, e.getMessage());
      tx.rollback();
      throw new RuntimeException(e);
    }
  }
}
