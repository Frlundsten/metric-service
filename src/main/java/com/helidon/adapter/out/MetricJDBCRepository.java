package com.helidon.adapter.out;

import static com.helidon.adapter.out.entity.MetricsEntity.*;

import com.helidon.adapter.RepositoryId;
import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricsEntity;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
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
  public static Logger LOG = LoggerFactory.getLogger(MetricJDBCRepository.class);
  private final DbClient dbClient;

  public MetricJDBCRepository(DbClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public void saveMetrics(Metrics metrics) {
    LOG.debug("Saving metrics {}", metrics);

    var repoID = RepositoryId.getScopedValue().value();
    var metricsEntity = fromDomain(metrics);

    var tx = dbClient.transaction();
    try {
      tx.createNamedInsert("insert-metrics")
          .params(
              metricsEntity.id(),
              metricsEntity.data(),
              Timestamp.from(metricsEntity.timestamp()),
              repoID)
          .execute();
      LOG.debug("Metric list size: {}", metricsEntity.metricList().size());
      for (MetricEntity entity : metricsEntity.metricList()) {
        tx.createNamedInsert("insert-metric")
            .params(entity.id(), entity.name(), metricsEntity.id(), entity.type(), entity.values())
            .execute();
      }
      tx.commit();
    } catch (Exception e) {
      LOG.error("Unable to save metrics.   {}", e.getMessage());
      tx.rollback();
    }
  }

  @Override
  public Metrics get(String id) {
    return null;
  }

  @Override
  public List<Metrics> getBetweenDates(Instant start, Instant end) {

    LOG.debug("Getting metrics between {} and {}", start, end);

    var tx = dbClient.transaction();
    var repoID = RepositoryId.getScopedValue().value();
    Map<String, MetricsEntity> resultMap = new HashMap<>();

    try {
      tx.createNamedQuery("get-between-dates")
          .params(Timestamp.from(start), Timestamp.from(end))
          .execute()
          .forEach(
              row -> {
                String metricsId = row.column("metrics_id").get(String.class);
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
                            new MetricsEntity(
                                metricsId, "{}", createdAt.toInstant(), new ArrayList<>()))
                    .metricList()
                    .add(metricEntity);
              });

      List<MetricsEntity> result = new ArrayList<>(resultMap.values());

      LOG.debug("FETCHED : {}", result);
      var domain = result.stream().map(MetricsEntity::toDomain).toList();
      return domain;
    } catch (Exception e) {
      LOG.error(
          "Unable to get metrics between {} and {} because of {}", start, end, e.getMessage());
      tx.rollback();
      throw new RuntimeException(e);
    }
  }
}
