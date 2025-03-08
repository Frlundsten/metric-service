package com.helidon.adapter.out;

import static com.helidon.adapter.out.entity.MetricsEntity.*;

import com.helidon.adapter.RepositoryId;
import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricsEntity;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
import com.helidon.exception.DatabaseInsertException;
import io.helidon.dbclient.DbClient;
import java.sql.Timestamp;
import java.util.List;
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

    var metricsEntity = fromDomain(metrics);

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

  @Override
  public List<Metrics> getBetweenDates(Instant start, Instant end) {
    Timestamp from = Timestamp.from(start);
    Timestamp to = Timestamp.from(end);

    var sql =
        """
        SELECT
            metrics.id AS metrics_id,
            metrics.created_at AS created_at,
            metric.id AS metric_id,
            metric.name,
            metric.type,
            metric.values
        FROM metric
                 JOIN metrics ON metric.metrics_id = metrics.id
        WHERE metrics.created_at BETWEEN ? AND ?
        """;

    try (var conn = dataSource.getConnection()) {

      try (var stmt = conn.prepareStatement(sql)) {
        stmt.setTimestamp(1, from);
        stmt.setTimestamp(2, to);

        var rs = stmt.executeQuery();
        List<MetricsEntity> metricsList = new ArrayList<>();
        List<MetricEntity> metricList = new ArrayList<>();

        Optional<String> current = Optional.empty();

        while (rs.next()) {
          var metricsId = rs.getString("metrics_id");
          var createdAt = rs.getTimestamp("created_at");
          var name = rs.getString("name");
          var type = rs.getString("type");
          var values = rs.getString("values");

          if (current.isEmpty() || !current.get().equals(metricsId)) {
            if (!metricList.isEmpty()) {
              metricsList.add(
                  new MetricsEntity(
                      current.orElse(""),
                      "{}",
                      createdAt.toInstant(),
                      new ArrayList<>(metricList)));
              metricList.clear();
            }
            current = Optional.of(metricsId);
          }

          metricList.add(new MetricEntity(name, type, values));
        }
        return metricsList.stream()
            .map(
                entity ->
                    new Metrics(
                        entity.id(),
                        entity.data(),
                        entity.timestamp(),
                        entity.metricList().stream().map(MetricEntity::toDomain).toList()))
            .toList();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
