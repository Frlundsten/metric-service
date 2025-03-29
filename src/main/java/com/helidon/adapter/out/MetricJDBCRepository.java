package com.helidon.adapter.out;

import static com.helidon.application.domain.model.K6Type.COUNTER;
import static com.helidon.application.domain.model.K6Type.GAUGE;
import static com.helidon.application.domain.model.K6Type.RATE;
import static com.helidon.application.domain.model.K6Type.TREND;

import com.helidon.application.domain.CounterValues;
import com.helidon.application.domain.GaugeValues;
import com.helidon.application.domain.RateValues;
import com.helidon.application.domain.RepositoryId;
import com.helidon.application.domain.TrendValues;
import com.helidon.application.domain.model.K6Metric;
import com.helidon.application.domain.model.K6Type;
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
    String sql = "INSERT INTO metrics VALUES (?, ?::JSON, ?,?)";
    var repositoryId = RepositoryId.getScopedValue().value();

    try (var stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, metrics.id());
      stmt.setString(2, metrics.data());
      stmt.setTimestamp(3, Timestamp.from(metrics.timestamp()));
      stmt.setString(4, repositoryId);

      int rows = stmt.executeUpdate();

      if (rows != 1) {
        LOG.error("Failed to insert metrics {}", metrics);
        conn.rollback();
        throw new DatabaseInsertException("Expected one row update but was: " + rows);
      }

    } catch (SQLException e) {
      conn.rollback();
      LOG.error("Rolling back transaction", e);
      throw e;
    }
  }

  private void saveMetrics(Connection conn, String metricsId, List<Metric> metrics)
      throws SQLException {
    String sql = "INSERT INTO metric VALUES (?, ?, ?, ?)";
    String counterValueSQL = "INSERT INTO counter_values VALUES(?,?,?)";
    String gaugeValueSQL = "INSERT INTO gauge_values VALUES(?,?,?,?)";
    String rateValueSQL = "INSERT INTO rate_values VALUES(?,?,?,?)";
    String trendValueSQL = "INSERT INTO trend_values VALUES(?,?,?,?,?,?,?)";

    try (var stmt = conn.prepareStatement(sql);
        var stmtCounter = conn.prepareStatement(counterValueSQL);
        var stmtGauge = conn.prepareStatement(gaugeValueSQL);
        var stmtRate = conn.prepareStatement(rateValueSQL);
        var stmtTrend = conn.prepareStatement(trendValueSQL)) {

      for (Metric metric : metrics) {
        stmt.setString(1, metric.id());
        stmt.setString(2, metric.name());
        stmt.setString(3, metricsId);
        stmt.setString(4, metric.type().getType());

        switch (metric.type()) {
          case GAUGE ->
              prepareGaugeValues(stmtGauge, (GaugeValues) metric.values(), metric.id()).addBatch();
          case TREND ->
              prepareTrendValues(stmtTrend, (TrendValues) metric.values(), metric.id()).addBatch();
          case COUNTER ->
              prepareCounterValues(stmtCounter, (CounterValues) metric.values(), metric.id())
                  .addBatch();
          case RATE ->
              prepareRateValues(stmtRate, (RateValues) metric.values(), metric.id()).addBatch();
          default -> {
            LOG.debug("Unknown metric type {}", metric.type());
            throw new IllegalStateException("Unexpected value: " + metric.type());
          }
        }
        stmt.addBatch();
      }

      var responseList = stmt.executeBatch();
      LOG.debug("Metrics inserted: {}", responseList.length);
      var counterValues = stmtCounter.executeBatch();
      LOG.debug("Counter values inserted: {}", counterValues.length);
      var gaugeValues = stmtGauge.executeBatch();
      LOG.debug("Gauge values inserted: {}", gaugeValues.length);
      var rateValues = stmtRate.executeBatch();
      LOG.debug("Rate values inserted: {}", rateValues.length);
      var trendValues = stmtTrend.executeBatch();
      LOG.debug("Trend values inserted: {}", trendValues.length);

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
  private PreparedStatement prepareRateValues(
      PreparedStatement stmtValues, RateValues values, String metric_id) throws SQLException {
    stmtValues.setString(1, metric_id);
    stmtValues.setDouble(2, values.rate());
    stmtValues.setDouble(3, values.passes());
    stmtValues.setDouble(4, values.fails());
    return stmtValues;
  }

  /*
  count
  rate
     */
  private PreparedStatement prepareCounterValues(
      PreparedStatement stmtValues, CounterValues values, String metric_id) throws SQLException {
    stmtValues.setString(1, metric_id);
    stmtValues.setDouble(2, values.count());
    stmtValues.setDouble(3, values.rate());
    return stmtValues;
  }

  /*
  avg
  min
  med
  max
  p(90)
  p(95)
     */
  private PreparedStatement prepareTrendValues(
      PreparedStatement stmtValues, TrendValues values, String metric_id) throws SQLException {
    stmtValues.setString(1, metric_id);
    stmtValues.setDouble(2, values.avg());
    stmtValues.setDouble(3, values.min());
    stmtValues.setDouble(4, values.med());
    stmtValues.setDouble(5, values.max());
    stmtValues.setDouble(6, values.p90());
    stmtValues.setDouble(7, values.p95());

    return stmtValues;
  }

  /*
  value
  min
  max
   */
  private PreparedStatement prepareGaugeValues(
      PreparedStatement stmtValues, GaugeValues values, String metric_id) throws SQLException {
    stmtValues.setString(1, metric_id);
    stmtValues.setDouble(2, values.value());
    stmtValues.setDouble(3, values.min());
    stmtValues.setDouble(4, values.max());
    return stmtValues;
  }

  @Override
  public Metrics get(String id) {
    return null;
  }

  @Override
  public List<Metrics> getBetweenDates(Instant start, Instant end) {
    Timestamp from = Timestamp.from(start);
    Timestamp to = Timestamp.from(end);

    // var sql = "SELECT * FROM metrics WHERE created_at BETWEEN ? AND ?";

    var sql =
        "SELECT metrics.*, metric.name, metric.type , metric_values.* FROM metrics INNER JOIN metric ON metric.metrics_id = metrics.id INNER JOIN metric_values ON metric_values.metric_id = metric.id WHERE created_at BETWEEN ? AND ?";

    try (var conn = dataSource.getConnection()) {

      try (var stmt = conn.prepareStatement(sql)) {
        stmt.setTimestamp(1, from);
        stmt.setTimestamp(2, to);

        var rs = stmt.executeQuery();
        List<Metrics> metricsList = new ArrayList<>();

        while (rs.next()) {
          var id = rs.getString("id");
          var data = rs.getString("data");
          var time = rs.getTimestamp("created_at").toInstant();
          var name = rs.getString("name");
          var type = rs.getString("type");

          Metric metric = new K6Metric(name, K6Type.valueOf(type), null);

          //          metricsList.add(new Metrics(List.of()));
        }
        return metricsList;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
