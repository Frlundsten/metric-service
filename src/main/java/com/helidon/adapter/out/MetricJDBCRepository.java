package com.helidon.adapter.out;

import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricJDBCRepository implements ForPersistingMetrics, ForManagingStoredMetrics {
  public static Logger log = LoggerFactory.getLogger(MetricJDBCRepository.class);
  private final DataSource dataSource;

  public MetricJDBCRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void saveMetrics(Metrics metrics) {
    log.debug("Saving metrics {}", metrics);

    try (var conn = dataSource.getConnection()) {
      conn.setAutoCommit(false);

      saveMetricsData(conn, metrics);
      saveMetrics(conn, metrics.id(), metrics.metricList());

      conn.commit();
    } catch (SQLException e) {
      log.error("Exception while saving metrics", e);
      throw new RuntimeException("Exception while saving metrics", e);
    }
  }

  private void saveMetricsData(Connection conn, Metrics metrics) throws SQLException {
    String sql = "INSERT INTO metrics VALUES (?, ?::JSON, ?)";

    try (var stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, metrics.id());
      stmt.setString(2, metrics.data());
      stmt.setTimestamp(3, Timestamp.from(metrics.timestamp()));

      int rows = stmt.executeUpdate();

      if (rows != 1) {
        throw new SQLException("Rows affected: " + rows);
      }

    } catch (SQLException e) {
      conn.rollback();
      log.error("Rolling back transaction");
      throw e;
    }
  }

  private void saveMetrics(Connection conn, String metricsId, List<Metric> metrics)
      throws SQLException {
    String sql = "INSERT INTO metric VALUES (?, ?, ?, ?)";

    try (var stmt = conn.prepareStatement(sql)) {

      for (Metric metric : metrics) {
        stmt.setString(1, metric.id());
        stmt.setString(2, metric.name());
        stmt.setString(3, metricsId);
        stmt.setString(4, metric.type().getType());

        stmt.addBatch();
      }

      var responseList = stmt.executeBatch();

      if (responseList.length != metrics.size()) {
        throw new SQLException("Batch update error. Rows affected: " + responseList.length);
      }

    } catch (SQLException e) {
      conn.rollback();
      log.error("Rolling back transaction");
      throw e;
    }
  }

  @Override
  public Metrics get(String id) {
    return null;
  }
}
