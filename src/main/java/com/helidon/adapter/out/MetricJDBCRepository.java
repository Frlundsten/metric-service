package com.helidon.adapter.out;

import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
import io.helidon.dbclient.DbClient;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricJDBCRepository implements ForPersistingMetrics, ForManagingStoredMetrics {
  public static Logger log = LoggerFactory.getLogger(MetricJDBCRepository.class);
  private final DbClient dbClient;

  public MetricJDBCRepository(DbClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public void saveMetrics(Metrics metrics) {
    log.debug("Saving metrics {}", metrics);

    var db = dbClient.transaction();
    try {
      db.namedInsert(
          "insertMetrics", metrics.id(), metrics.data(), Timestamp.from(metrics.timestamp()));
      for (Metric metric : metrics.metricList()) {
        db.namedInsert(
            "insertSingleMetric",
            metric.id(),
            metric.name(),
            metrics.id(),
            metric.type().getType());
      }

      db.commit();
    } catch (Exception e) {
      log.error(e.getMessage());
      db.rollback();
    }
  }

  @Override
  public Metrics get(String id) {
    return null;
  }
}
