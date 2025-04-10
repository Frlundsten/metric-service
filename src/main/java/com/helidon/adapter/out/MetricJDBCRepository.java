package com.helidon.adapter.out;

import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricsEntity;
import com.helidon.application.RepositoryId;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
import com.helidon.exception.DatabaseInsertException;
import com.helidon.exception.EmptyMetricListException;
import com.helidon.util.Mapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricJDBCRepository implements ForPersistingMetrics, ForManagingStoredMetrics {
  public static final Logger LOG = LoggerFactory.getLogger(MetricJDBCRepository.class);
  private final DataSource dataSource;
  private final Mapper mapper;

  public MetricJDBCRepository(DataSource dataSource, Mapper mapper) {
    this.dataSource = dataSource;
    this.mapper = mapper;
  }

  @Override
  public void saveMetrics(Metrics metrics) {
    LOG.debug("Saving metrics {}", metrics);

    var metricsEntity = mapper.toEntity(metrics);

    try (var conn = dataSource.getConnection()) {
      conn.setAutoCommit(false);
      saveMetricsData(conn, metricsEntity);
      saveMetrics(conn, metricsEntity.id(), metricsEntity.metricList());
      conn.commit();
      LOG.debug("Metrics saved to database");
    } catch (DatabaseInsertException e) {
      LOG.error(e.getMessage());
    } catch (SQLException e) {
      throw new DatabaseInsertException("Error when inserting", e);
    }
  }

  private void saveMetricsData(Connection conn, MetricsEntity metricsToPersist)
      throws SQLException {
    String sql = "INSERT INTO metrics" + " VALUES (?, ?::json, ?,?)";
    var repositoryId = RepositoryId.getScopedValue().value();

    try (var stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, metricsToPersist.id());
      stmt.setString(2, metricsToPersist.data());
      stmt.setTimestamp(3, Timestamp.from(metricsToPersist.timestamp()));
      stmt.setString(4, repositoryId);

      int rows = stmt.executeUpdate();

      if (rows != 1) {
        LOG.error("Failed to insert metrics {}", metricsToPersist);
        conn.rollback();
        throw new DatabaseInsertException("Expected one row update but was: " + rows);
      }

    } catch (SQLException e) {
      conn.rollback();
      throw e;
    } catch (DatabaseInsertException e) {
      conn.rollback();
      LOG.error("Unable to insert metrics {}", metricsToPersist);
    }
  }

  private void saveMetrics(Connection conn, String metricsId, List<MetricEntity> metrics)
      throws SQLException {
    if (metrics.isEmpty()) {
      LOG.error("No metrics to save");
      throw new EmptyMetricListException("No metrics to save");
    }
    LOG.debug(
        "Saving ({}) metrics collected from repository ({})",
        metrics.size(),
        RepositoryId.getScopedValue());
    String sql = "INSERT INTO metric VALUES (?, ?, ?, ?, ?::jsonb)";

    try (var stmt = conn.prepareStatement(sql)) {
      stmt.setString(3, metricsId);
      for (MetricEntity metric : metrics) {
        stmt.setString(1, metric.id());
        stmt.setString(2, metric.name());
        stmt.setString(4, metric.type());
        stmt.setString(5, metric.values());
        stmt.addBatch();
      }
      stmt.executeBatch();
    } catch (SQLException e) {
      conn.rollback();
      throw new DatabaseInsertException("Error when saving metric", e);
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
                      current.get(), "{}", createdAt.toInstant(), new ArrayList<>(metricList)));
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
                        entity.metricList().stream().map(mapper::toDomain).toList()))
            .toList();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
