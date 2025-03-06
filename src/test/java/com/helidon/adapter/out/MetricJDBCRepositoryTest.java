package com.helidon.adapter.out;

import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.helidon.application.domain.model.Metrics;
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

  @InjectMocks
  MetricJDBCRepository repository;

  @Test
  void shouldNotThrowExceptionWhenSavingValidData() throws SQLException {
    Metrics metrics = new Metrics("{}", List.of());

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(1);

    assertThatNoException().isThrownBy(() -> repository.saveMetrics(metrics));
    verify(connection, times(1)).close();
    verify(preparedStatement, times(1)).close();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2})
  void shouldThrowWhenUpdatedRowsIsNotOne(int rows) throws SQLException {
    Metrics metrics = new Metrics("{}", List.of());

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(rows);

    assertThatException().isThrownBy(() -> repository.saveMetrics(metrics));
    verify(connection, times(1)).close();
    verify(preparedStatement, times(1)).close();
  }
}
