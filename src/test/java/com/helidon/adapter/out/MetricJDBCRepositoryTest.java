package com.helidon.adapter.out;

import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.helidon.application.domain.model.K6Metric;
import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.domain.model.Values;
import com.helidon.exception.DatabaseInsertException;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbTransaction;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetricJDBCRepositoryTest {

  @Mock DbClient dbClient;
  @Mock DbTransaction dbTransaction;

  @InjectMocks MetricJDBCRepository repository;

  @Test
  void shouldNotThrowExceptionWhenSavingValidData() throws SQLException {
    Metric metric = new K6Metric("http-test", K6Type.COUNTER, mock(Values.class));
    Metrics metrics = new Metrics("{}", List.of(metric));

    when(dbClient.transaction()).thenReturn(dbTransaction);
    when(dbTransaction.namedInsert(anyString(), any(Object[].class))).thenReturn(1L);

    repository.saveMetrics(metrics);

    verify(dbTransaction).commit();
    verify(dbTransaction)
        .namedInsert(
            "insertMetrics", metrics.id(), metrics.data(), Timestamp.from(metrics.timestamp()));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2})
  void shouldThrowWhenUpdatedRowsIsNotOne(int rows) throws SQLException {
    Metrics metrics = new Metrics("{}", List.of());

    when(dbClient.transaction()).thenReturn(dbTransaction);
    when(dbTransaction.namedInsert(anyString(), any(Object[].class))).thenReturn((long) rows);

    assertThatException()
        .isThrownBy(() -> repository.saveMetrics(metrics))
        .isInstanceOf(DatabaseInsertException.class)
        .withMessage("Expected only one row");
  }
}
