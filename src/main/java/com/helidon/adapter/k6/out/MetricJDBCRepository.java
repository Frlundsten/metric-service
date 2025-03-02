package com.helidon.adapter.k6.out;

import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.Repository;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricJDBCRepository implements Repository {
  public static Logger log = LoggerFactory.getLogger(MetricJDBCRepository.class);
  private final DataSource dataSource;

  public MetricJDBCRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void save(Metrics metrics) {
    log.info("Saving metrics {}", metrics);

    String sql = "INSERT INTO metrics VALUES (?, ?::JSON, ?)";

    try (var conn = dataSource.getConnection()) {
      conn.setAutoCommit(false);
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
      conn.commit();
    } catch (SQLException e) {
      log.error("Exception while saving metrics", e);
      throw new RuntimeException("Exception while saving metrics", e);
    }
  }

  @Override
  public Metrics get(String id) {
    return null;
  }
}
