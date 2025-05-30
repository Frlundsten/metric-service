package com.helidon.adapter.out;

import static com.helidon.MetricFactory.createMetricReport;
import static com.helidon.MetricFactory.createMetricReportWithEmptyList;
import static com.helidon.ProvideScope.withScope;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.params.provider.Arguments.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.helidon.MetricFactory;
import com.fl.adapter.out.persistence.MetricJDBCRepository;
import com.fl.application.domain.model.MetricReport;
import com.fl.exception.EmptyMetricListException;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbExecute;
import io.helidon.dbclient.DbStatementDml;
import io.helidon.dbclient.DbStatementQuery;
import io.helidon.dbclient.DbTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MetricJDBCRepositoryTest {

  private static DbClient dbClient;
  private static DbTransaction dbTransaction;
  private static MetricJDBCRepository repository;
  private static DbStatementDml dbStatement;
  private static DbStatementDml dbStatementReport;
  private static DbStatementQuery dbStatementQuery;
  private static Connection conn;
  private static final String reportSql =
      "INSERT INTO metric_report VALUES (?::UUID, ?::JSON, ?, ?)";
  private static final String metricSql =
      "INSERT INTO metric VALUES (?::UUID, ?, ?::UUID, ?, ?::JSONB)";
  private static PreparedStatement stmtReport;
  private static PreparedStatement stmtEntity;

  @BeforeAll
  static void setUp() {
    dbClient = mock(DbClient.class);
    dbTransaction = mock(DbTransaction.class);
    dbStatementReport = mock(DbStatementDml.class);
    dbStatement = mock(DbStatementDml.class);
    dbStatementQuery = mock(DbStatementQuery.class);
    repository = new MetricJDBCRepository(dbClient);
    conn = mock(Connection.class);
    stmtReport = mock(PreparedStatement.class);
    stmtEntity = mock(PreparedStatement.class);
  }

  @BeforeEach
  void resetMocks() {
    reset(
        dbClient,
        dbTransaction,
        dbStatementReport,
        dbStatement,
        dbStatementQuery,
        conn,
        stmtReport,
        stmtEntity);
  }

  @Test
  void shouldNotThrowExceptionWhenSavingValidData() throws SQLException {
    MetricReport metricReport = createMetricReport();

    DbExecute execute = mock(DbExecute.class);
    when(dbClient.execute()).thenReturn(execute);
    when(execute.createDmlStatement(anyString())).thenReturn(dbStatement);
    when(conn.prepareStatement(reportSql)).thenReturn(stmtReport);
    when(conn.prepareStatement(metricSql)).thenReturn(stmtEntity);
    when(dbClient.unwrap(Connection.class)).thenReturn(conn);
    when(stmtEntity.executeBatch()).thenReturn(new int[] {1});
    when(stmtReport.executeUpdate()).thenReturn(1);

    assertThatNoException().isThrownBy(() -> withScope(() -> repository.saveMetrics(metricReport)));

    verify(conn).commit();
    verify(stmtEntity).executeBatch();
    verify(stmtReport).executeUpdate();
    verify(stmtEntity).close();
    verify(stmtReport).close();
  }

  static Stream<Arguments> exceptions() {
    return Stream.of(
        arguments(0, new int[] {1}, "Failed to insert report: "),
        arguments(2, new int[] {2}, "Failed to insert report: "),
        arguments(1, new int[] {}, "Failed to insert report: "),
        arguments(1, new int[] {1, 1, 1, 1}, "Failed to insert report: "),
        arguments(1, new int[] {2}, "Failed to insert report: "));
  }

  @ParameterizedTest
  @MethodSource("exceptions")
  void shouldThrowWhenUpdatedRowsIsNotOne(
      int reportUpdates, int[] entityUpdates, String errorMessage) throws SQLException {
    MetricReport metricReport = MetricFactory.createMetricReport();

    when(dbClient.unwrap(Connection.class)).thenReturn(conn);
    when(conn.prepareStatement(metricSql)).thenReturn(stmtEntity);
    when(conn.prepareStatement(reportSql)).thenReturn(stmtReport);

    when(stmtEntity.executeBatch()).thenReturn(entityUpdates);
    when(stmtReport.executeUpdate()).thenReturn(reportUpdates);

    assertThatException()
        .isThrownBy(() -> withScope(() -> repository.saveMetrics(metricReport)))
        .withMessageContaining(errorMessage);

    verify(conn, never()).commit();
    verify(conn).rollback();
  }

  @Test
  void shouldThrowWhenSavingMetricsWithEmptyList() {
    MetricReport metrics = createMetricReportWithEmptyList();

    when(dbClient.transaction()).thenReturn(dbTransaction);

    assertThatException()
        .isThrownBy(() -> withScope(() -> repository.saveMetrics(metrics)))
        .isInstanceOf(EmptyMetricListException.class)
        .withMessage("No metrics found in report");
  }

  @Test
  void shouldGetBetweenDates() {
    Instant from = Instant.now().minus(1, ChronoUnit.DAYS);
    Instant to = Instant.now();

    DbExecute execute = mock(DbExecute.class);
    when(dbClient.execute()).thenReturn(execute);
    when(execute.createNamedQuery("get-between-dates")).thenReturn(dbStatementQuery);
    when(dbStatementQuery.params(any(Object[].class))).thenReturn(dbStatementQuery);

    assertThatNoException().isThrownBy(() -> withScope(() -> repository.getBetweenDates(from, to)));
  }

  @Test
  void shouldReturnEmptyListWhenExceptionOccurs() {
    Instant from = Instant.now().minus(1, ChronoUnit.DAYS);
    Instant to = Instant.now();
    DbExecute execute = mock(DbExecute.class);
    when(dbClient.execute()).thenReturn(execute);
    when(execute.createNamedQuery("get-between-dates")).thenReturn(dbStatementQuery);
    when(dbStatementQuery.params(any(Object[].class))).thenReturn(dbStatementQuery);
    when(dbStatementQuery.execute()).thenThrow(new RuntimeException("DB failure"));

    assertThatException()
        .isThrownBy(() -> withScope(() -> repository.getBetweenDates(from, to)))
        .isInstanceOf(RuntimeException.class)
        .withMessageContaining("DB failure");
  }
}
