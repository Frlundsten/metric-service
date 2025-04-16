package com.helidon.adapter.out;

import static com.helidon.MetricFactory.createMetricsWithEmptyList;
import static com.helidon.MetricFactory.createMetricsWithList;
import static com.helidon.ProvideScope.withScope;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.helidon.application.domain.model.Metrics;
import com.helidon.exception.DatabaseInsertException;
import com.helidon.exception.EmptyMetricListException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

class MetricJDBCRepositoryTest {

  public static DataSource dataSource;
  public static Connection connection;
  public static PreparedStatement preparedStatement;
  public static MetricJDBCRepository repository;

  @BeforeAll
  static void setUp() {
    dataSource = mock(DataSource.class);
    connection = mock(Connection.class);
    preparedStatement = mock(PreparedStatement.class);
    repository = new MetricJDBCRepository(dataSource);
  }

  @BeforeEach
  void resetMocks() {
    Mockito.reset(dataSource, connection, preparedStatement);
  }

  @Test
  void shouldNotThrowExceptionWhenSavingValidData() throws SQLException {
    Metrics metrics = createMetricsWithList();

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(1);
    when(preparedStatement.executeBatch()).thenReturn(new int[] {1});

    assertThatNoException().isThrownBy(() -> withScope(() -> repository.saveMetrics(metrics)));
    verify(connection, times(1)).close();
    verify(preparedStatement, times(2)).close();
    verify(connection, never()).rollback();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2})
  void shouldThrowWhenUpdatedRowsIsNotOne(int rows) throws SQLException {
    Metrics metrics = createMetricsWithList();

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(rows);

    assertThatException()
        .isThrownBy(() -> withScope(() -> repository.saveMetrics(metrics)))
        .isInstanceOf(DatabaseInsertException.class)
        .withMessage("Expected one row update but was: " + rows);
    verify(connection, times(1)).close();
    verify(preparedStatement, times(1)).close();
    verify(connection, times(1)).rollback();
  }

  @Test
  void shouldThrowWhenSavingMetricsWithEmptyList() throws SQLException {
    Metrics metrics = createMetricsWithEmptyList();
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(1);

    assertThatException()
        .isThrownBy(() -> withScope(() -> repository.saveMetrics(metrics)))
        .isInstanceOf(EmptyMetricListException.class)
        .withMessage("No metrics to save");
    verify(connection, times(1)).close();
    verify(preparedStatement, times(1)).close();
    verify(connection, never()).commit();
    verify(connection, never()).rollback();
  }

  @Test
  void shouldRollbackOnSQLExceptionWhenSavingMetricsObject() throws SQLException {
    Metrics metrics = createMetricsWithList();
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

    assertThatException()
        .isThrownBy(() -> withScope(() -> repository.saveMetrics(metrics)))
        .isInstanceOf(DatabaseInsertException.class)
        .withMessage("Error when inserting");
    verify(connection, times(1)).close();
    verify(preparedStatement, times(1)).close();
    verify(connection, times(1)).rollback();
  }

  @Test
  void shouldRollbackOnSQLExceptionWhenSavingAllMetrics() throws SQLException {
    Metrics metrics = createMetricsWithList();
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(1);
    when(preparedStatement.executeBatch()).thenThrow(new SQLException());

    assertThatException()
        .isThrownBy(() -> withScope(() -> repository.saveMetrics(metrics)))
        .isInstanceOf(DatabaseInsertException.class)
        .withMessage("Error when saving metric");
    verify(connection, times(1)).close();
    verify(preparedStatement, times(2)).close();
    verify(connection, times(1)).rollback();
  }
}
