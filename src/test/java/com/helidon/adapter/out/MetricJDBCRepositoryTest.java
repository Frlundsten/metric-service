package com.helidon.adapter.out;

import static com.helidon.MetricFactory.createMetricReport;
import static com.helidon.MetricFactory.createMetricReportWithEmptyList;
import static com.helidon.ProvideScope.withScope;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.junit.jupiter.params.provider.Arguments.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.helidon.MetricFactory;
import com.helidon.adapter.RepositoryId;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.MetricReport;
import com.helidon.exception.EmptyMetricListException;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbRow;
import io.helidon.dbclient.DbStatementDml;
import io.helidon.dbclient.DbStatementQuery;
import io.helidon.dbclient.DbTransaction;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MetricJDBCRepositoryTest {

  static DbClient dbClient;
  static DbTransaction dbTransaction;
  static MetricJDBCRepository repository;
  static DbStatementDml dbStatement;
  static DbStatementDml dbStatementReport;
  static DbStatementQuery dbStatementQuery;

  @BeforeAll
  static void setUp() {
    dbClient = mock(DbClient.class);
    dbTransaction = mock(DbTransaction.class);
    dbStatementReport = mock(DbStatementDml.class);
    dbStatement = mock(DbStatementDml.class);
    dbStatementQuery = mock(DbStatementQuery.class);
    repository = new MetricJDBCRepository(dbClient);
  }

  @BeforeEach
  void resetMocks() {
    reset(dbClient, dbTransaction, dbStatement, dbStatementReport);
  }

  @Test
  void shouldNotThrowExceptionWhenSavingValidData() {
    MetricReport metricReport = createMetricReport();
    var repoId = withScope(() -> RepositoryId.getScopedValue().value());

    when(dbClient.transaction()).thenReturn(dbTransaction);
    when(dbTransaction.createNamedInsert("insert-metric-report")).thenReturn(dbStatementReport);
    when(dbTransaction.createNamedInsert("insert-metric")).thenReturn(dbStatement);
    when(dbStatementReport.execute()).thenReturn(1L);
    when(dbStatement.execute()).thenReturn(1L);

    when(dbStatementReport.params(
            metricReport.id(),
            metricReport.data(),
            Timestamp.from(metricReport.timestamp()),
            repoId))
        .thenReturn(dbStatementReport);
    when(dbStatement.params(
            anyString(), anyString(), eq(metricReport.id()), anyString(), anyString()))
        .thenReturn(dbStatement);

    withScope(() -> repository.saveMetrics(metricReport));

    verify(dbTransaction).commit();
    verify(dbStatementReport, times(1))
        .params(
            metricReport.id(),
            metricReport.data(),
            Timestamp.from(metricReport.timestamp()),
            repoId);
  }

  static Stream<Arguments> exceptions() {
    return Stream.of(
        arguments(0, 1, "Failed to insert report: "),
        arguments(2, 2, "Failed to insert report: "),
        arguments(1, 0, "Failed to insert entity: "),
        arguments(1, 2, "Failed to insert entity: "));
  }

  @ParameterizedTest
  @MethodSource("exceptions")
  void shouldThrowWhenUpdatedRowsIsNotOne(
      long reportUpdates, long entityUpdates, String errorMessage) {
    MetricReport metricReport = MetricFactory.createMetricReport();

    when(dbClient.transaction()).thenReturn(dbTransaction);
    when(dbTransaction.createNamedInsert("insert-metric-report")).thenReturn(dbStatementReport);
    when(dbTransaction.createNamedInsert("insert-metric")).thenReturn(dbStatement);
    when(dbStatement.params(any(Object[].class))).thenReturn(dbStatement);
    when(dbStatementReport.params(any(Object[].class))).thenReturn(dbStatementReport);
    when(dbStatement.execute()).thenReturn(entityUpdates);
    when(dbStatementReport.execute()).thenReturn(reportUpdates);

    assertThatException()
        .isThrownBy(() -> withScope(() -> repository.saveMetrics(metricReport)))
        .withMessageContaining(errorMessage);

    verify(dbTransaction, never()).commit();
    verify(dbTransaction).rollback();
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
    Timestamp expectedTimestamp = Timestamp.from(from);
    String reportId = UUID.randomUUID().toString();
    String entityId = UUID.randomUUID().toString();

    DbRow row = mock(DbRow.class);
    DbColumn metricsId = mock(DbColumn.class);
    DbColumn createdAt = mock(DbColumn.class);
    DbColumn metricId = mock(DbColumn.class);
    DbColumn name = mock(DbColumn.class);
    DbColumn type = mock(DbColumn.class);
    DbColumn values = mock(DbColumn.class);

    when(row.column("report_id")).thenReturn(metricsId);
    when(row.column("created_at")).thenReturn(createdAt);
    when(row.column("metric_id")).thenReturn(metricId);
    when(row.column("name")).thenReturn(name);
    when(row.column("type")).thenReturn(type);
    when(row.column("values")).thenReturn(values);

    when(createdAt.get(Timestamp.class)).thenReturn(expectedTimestamp);
    when(metricsId.get(String.class)).thenReturn(reportId);
    when(metricId.get(String.class)).thenReturn(entityId);
    when(name.get(String.class)).thenReturn("metric_name");
    when(type.get(String.class)).thenReturn("rate");
    when(values.get(String.class))
        .thenReturn(
"""
  {
      "rate": 0,
      "passes": 0,
      "fails": 100
  }
""");

    Stream<DbRow> rows = Stream.of(row);

    when(dbClient.transaction()).thenReturn(dbTransaction);
    when(dbTransaction.createNamedQuery("get-between-dates")).thenReturn(dbStatementQuery);
    when(dbStatementQuery.params(any(Object[].class))).thenReturn(dbStatementQuery);
    when(dbStatementQuery.execute()).thenReturn(rows);

    var result = withScope(()-> repository.getBetweenDates(from, to));

    assertThat(result).isNotEmpty();
    var report = result.getFirst();

    assertThat(report).isNotNull().isInstanceOf(MetricReport.class);
    var entity = report.metricList().getFirst();

    assertThat(entity).isNotNull().isInstanceOf(Metric.class);
    assertThat(entity.name().value()).isEqualTo("metric_name");
    assertThat(entity.type().name()).isEqualTo("RATE");
  }
}
