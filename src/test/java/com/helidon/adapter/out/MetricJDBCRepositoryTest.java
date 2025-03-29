package com.helidon.adapter.out;

import static com.helidon.ProvideScope.withScope;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.helidon.application.domain.CounterValues;
import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Metrics;
import com.helidon.exception.DatabaseInsertException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetricJDBCRepositoryTest {

  @Mock DataSource dataSource;
  @Mock Connection connection;
  @Mock PreparedStatement preparedStatement;

  @InjectMocks MetricJDBCRepository repository;

  @Test
  void shouldNotThrowExceptionWhenSavingValidData() throws SQLException {
    Metric metric = mock(Metric.class);
    when(metric.type()).thenReturn(K6Type.COUNTER);
    CounterValues val = new CounterValues(2.0, 2.0);
    when(metric.values()).thenReturn(val);
    Metrics metrics = new Metrics("{}", List.of(metric));

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(1);
    when(preparedStatement.executeBatch()).thenReturn(new int[] {1});

    assertThatNoException().isThrownBy(() -> withScope(() -> repository.saveMetrics(metrics)));
    verify(connection, times(1)).close();
    verify(preparedStatement, times(6)).close();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2})
  void shouldThrowWhenUpdatedRowsIsNotOne(int rows) throws SQLException {
    Metrics metrics = new Metrics("{}", List.of());

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(rows);

    assertThatException()
        .isThrownBy(() -> withScope(() -> repository.saveMetrics(metrics)))
        .isInstanceOf(DatabaseInsertException.class)
        .withMessage("Expected one row update but was: " + rows);
    verify(connection, times(1)).close();
    verify(preparedStatement, times(1)).close();
  }
}
