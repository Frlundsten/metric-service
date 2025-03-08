package com.helidon.adapter.out;

import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
import com.helidon.exception.DatabaseInsertException;
import io.helidon.dbclient.DbClient;
import java.sql.Timestamp;
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

    var db = dbClient.transaction();
    try {
      var expectedRows =
          db.namedInsert(
              "insertMetrics", metrics.id(), metrics.data(), Timestamp.from(metrics.timestamp()));

      if (expectedRows != 1) {
        throw new DatabaseInsertException("Expected only one row");
      }

      for (Metric metric : metrics.metricList()) {
        var rowPerMetric =
            db.namedInsert(
                "insertSingleMetric",
                metric.id(),
                metric.name(),
                metrics.id(),
                metric.type().getType());

        if (rowPerMetric != 1) {
          throw new DatabaseInsertException("Expected only one row");
        }
      }

      db.commit();
    } catch (Exception e) {
      LOG.error(e.getMessage());
      db.rollback();
      throw e;
    }
  }

  @Override
  public Metrics get(String id) {
    return null;
  }
}
