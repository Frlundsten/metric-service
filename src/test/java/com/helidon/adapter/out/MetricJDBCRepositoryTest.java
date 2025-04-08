package com.helidon.adapter.out;

import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricsEntity;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Metrics;
import com.helidon.exception.DatabaseInsertException;
import com.helidon.util.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import static com.helidon.ProvideScope.withScope;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricJDBCRepositoryTest {

    @Mock
    DataSource dataSource;
    @Mock
    Connection connection;
    @Mock
    PreparedStatement preparedStatement;
    @Mock
    Mapper mapper;
    @InjectMocks
    MetricJDBCRepository repository;

    @Test
    void shouldNotThrowExceptionWhenSavingValidData() throws SQLException {
        lenient().when(mapper.toEntity(any())).thenReturn(new MetricsEntity("{\"key\":\"val\"}", Instant.now(), List.of(new MetricEntity("http", "rate", "{\"avg\":5.0}"))));
        Metric metric = mock(Metric.class);
        Metrics k6Metrics = new Metrics("{}", List.of(metric));

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1});

        assertThatNoException().isThrownBy(() -> withScope(() -> repository.saveMetrics(k6Metrics)));
        verify(connection, times(1)).close();
        verify(preparedStatement, times(2)).close();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2})
    void shouldThrowWhenUpdatedRowsIsNotOne(int rows) throws SQLException {
        lenient().when(mapper.toEntity(any())).thenReturn(new MetricsEntity("{\"key\":\"val\"}", Instant.now(), List.of(new MetricEntity("http", "rate", "{\"avg\":5.0}"))));
        Metrics k6Metrics = new Metrics("{}", List.of());

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(rows);

        assertThatException()
                .isThrownBy(() -> withScope(() -> repository.saveMetrics(k6Metrics)))
                .isInstanceOf(DatabaseInsertException.class)
                .withMessage("Expected one row update but was: " + rows);
        verify(connection, times(1)).close();
        verify(preparedStatement, times(1)).close();
    }
}
