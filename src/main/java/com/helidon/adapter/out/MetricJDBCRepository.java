package com.helidon.adapter.out;

import static com.helidon.application.domain.model.K6Type.COUNTER;
import static com.helidon.application.domain.model.K6Type.GAUGE;
import static com.helidon.application.domain.model.K6Type.RATE;
import static com.helidon.application.domain.model.K6Type.TREND;
import static java.sql.Types.DECIMAL;

import com.helidon.application.domain.CounterValues;
import com.helidon.application.domain.GaugeValues;
import com.helidon.application.domain.RateValues;
import com.helidon.application.domain.TrendValues;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
import com.helidon.exception.DatabaseInsertException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricJDBCRepository implements ForPersistingMetrics, ForManagingStoredMetrics {
  public static Logger LOG = LoggerFactory.getLogger(MetricJDBCRepository.class);
  private final DataSource dataSource;

  public MetricJDBCRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void saveMetrics(Metrics metrics) {
    LOG.debug("Saving metrics {}", metrics);

    try (var conn = dataSource.getConnection()) {
      conn.setAutoCommit(false);

      saveMetricsData(conn, metrics);
      saveMetrics(conn, metrics.id(), metrics.metricList());

      conn.commit();
    } catch (DatabaseInsertException e) {
      LOG.error(e.getMessage());
      throw e;
    } catch (SQLException e) {
      LOG.error("Exception while saving metrics", e);
      throw new DatabaseInsertException(e.getMessage());
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
        throw new DatabaseInsertException("Expected one row update but was: " + rows);
      }

    } catch (SQLException e) {
      conn.rollback();
      LOG.error("Rolling back transaction");
      throw e;
    }
  }

  private void saveMetrics(Connection conn, String metricsId, List<Metric> metrics)
      throws SQLException {
    String sql = "INSERT INTO metric VALUES (?, ?, ?, ?)";
    String valuesSql = "INSERT INTO metric_values VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

    try (var stmt = conn.prepareStatement(sql);
        var stmtValues = conn.prepareStatement(valuesSql)) {

      for (Metric metric : metrics) {
        LOG.debug("Saving metric {}", metric);
        stmt.setString(1, metric.id());
        stmt.setString(2, metric.name());
        stmt.setString(3, metricsId);
        stmt.setString(4, metric.type().getType());

        stmtValues.setString(1, UUID.randomUUID().toString());
        stmtValues.setString(2, metric.id());

        switch (metric.type()) {
          case GAUGE -> prepareGaugeValues(stmtValues, (GaugeValues) metric.values());
          case TREND -> prepareTrendValues(stmtValues, (TrendValues) metric.values());
          case COUNTER -> prepareCounterValues(stmtValues, (CounterValues) metric.values());
          case RATE -> prepareRateValues(stmtValues, (RateValues) metric.values());
          default -> throw new IllegalStateException("Unexpected value: " + metric.type());
        }

        LOG.debug("Saving vals {}", stmtValues);

        stmtValues.addBatch();
        stmt.addBatch();
      }

      var responseList = stmt.executeBatch();
      LOG.debug("Metrics inserted: {}", responseList.length);
      var values = stmtValues.executeBatch();
      LOG.debug("Metric values inserted: {}", values.length);

      if (values.length != metrics.size() || responseList.length != metrics.size()) {
        throw new DatabaseInsertException(
            "Batch update error. Rows affected: " + responseList.length);
      }

    } catch (SQLException e) {
      conn.rollback();
      LOG.error("Error when saving metric", e);
      throw e;
    }
  }

  /*
  rate
  passes
  fails
     */
  private void prepareRateValues(PreparedStatement stmtValues, RateValues values)
      throws SQLException {
    stmtValues.setNull(3, DECIMAL);
    stmtValues.setNull(4, DECIMAL);
    stmtValues.setNull(5, DECIMAL);
    stmtValues.setNull(6, DECIMAL);

    stmtValues.setDouble(7, values.rate());
    stmtValues.setDouble(8, values.passes());
    stmtValues.setDouble(9, values.fails());

    stmtValues.setNull(10, DECIMAL);
    stmtValues.setNull(11, DECIMAL);
    stmtValues.setNull(12, DECIMAL);
    stmtValues.setNull(13, DECIMAL);
  }

  /*
  count
  rate
     */
  private void prepareCounterValues(PreparedStatement stmtValues, CounterValues values)
      throws SQLException {
    stmtValues.setNull(4, DECIMAL);
    stmtValues.setNull(5, DECIMAL);
    stmtValues.setNull(6, DECIMAL);

    stmtValues.setDouble(3, values.count());
    stmtValues.setDouble(7, values.rate());

    stmtValues.setNull(8, DECIMAL);
    stmtValues.setNull(9, DECIMAL);
    stmtValues.setNull(10, DECIMAL);
    stmtValues.setNull(11, DECIMAL);
    stmtValues.setNull(12, DECIMAL);
    stmtValues.setNull(13, DECIMAL);
  }

  /*
  avg
  min
  med
  max
  p(90)
  p(95)
     */
  private void prepareTrendValues(PreparedStatement stmtValues, TrendValues values)
      throws SQLException {
    stmtValues.setDouble(10, values.avg());
    stmtValues.setDouble(5, values.min());
    stmtValues.setDouble(11, values.med());
    stmtValues.setDouble(6, values.max());
    stmtValues.setDouble(12, values.p90());
    stmtValues.setDouble(13, values.p95());

    stmtValues.setNull(3, DECIMAL);
    stmtValues.setNull(4, DECIMAL);
    stmtValues.setNull(7, DECIMAL);
    stmtValues.setNull(8, DECIMAL);
    stmtValues.setNull(9, DECIMAL);
  }

  /*
  value
  min
  max
   */
  private void prepareGaugeValues(PreparedStatement stmtValues, GaugeValues values)
      throws SQLException {
    stmtValues.setDouble(4, values.value());
    stmtValues.setDouble(5, values.min());
    stmtValues.setDouble(6, values.max());

    stmtValues.setNull(3, DECIMAL);
    stmtValues.setNull(7, DECIMAL);
    stmtValues.setNull(8, DECIMAL);
    stmtValues.setNull(9, DECIMAL);
    stmtValues.setNull(10, DECIMAL);
    stmtValues.setNull(11, DECIMAL);
    stmtValues.setNull(12, DECIMAL);
    stmtValues.setNull(13, DECIMAL);
  }

  @Override
  public Metrics get(String id) {
    return null;
  }

  @Override
  public List<Metrics> getBetweenDates(Instant start, Instant end) {
    Timestamp from = Timestamp.from(start);
    Timestamp to = Timestamp.from(end);

    var sql = "SELECT * FROM metrics WHERE created_at BETWEEN ? AND ?";

    try (var conn = dataSource.getConnection()) {

      try (var stmt = conn.prepareStatement(sql)) {
        stmt.setTimestamp(1, from);
        stmt.setTimestamp(2, to);

        var rs = stmt.executeQuery();
        List<Metrics> metricsList = new ArrayList<>();
        while (rs.next()) {
          metricsList.add(
              new Metrics(
                  rs.getString("id"),
                  rs.getString("data"),
                  rs.getTimestamp("created_at").toInstant(),
                  List.of()));
        }
        return metricsList;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
