package com.helidon.adapter.out;

import com.helidon.adapter.in.rest.RepositoryId;
import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricsEntity;
import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.MetricName;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
import com.helidon.exception.DatabaseInsertException;
import com.helidon.util.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

        var metricsEntity
                = mapper.toEntity(metrics);

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

    private void saveMetricsData(Connection conn, MetricsEntity metricsToPersist
    ) throws SQLException {
        String sql = "INSERT INTO metrics" +
                " VALUES (?, ?::json, ?,?)";
        var repositoryId = RepositoryId.getScopedValue().value();

        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, metricsToPersist
                    .id());
            stmt.setString(2, metricsToPersist
                    .data());
            stmt.setTimestamp(3, Timestamp.from(metricsToPersist
                    .timestamp()));
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
        String sql = "INSERT INTO metric VALUES (?, ?, ?, ?, ?::jsonb)";

        try (var stmt = conn.prepareStatement(sql)
        ) {
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

        // var sql = "SELECT * FROM metrics WHERE created_at BETWEEN ? AND ?";

        var sql =
                """
                SELECT 
                """;
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

                    Metric metric = new Metric(new MetricName(name), K6Type.valueOf(type), null);

                    //          metricsList.add(new Metrics(List.of()));
                }
                return metricsList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
