package com.helidon.adapter.application.domain;

import com.helidon.adapter.application.k6.domain.K6TrendValues;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class K6TrendValuesTest {

    @Test
    void shouldCreateTrendValues() {
        var trendValues = new K6TrendValues(1005.6491, 1000.3286, 1001.3614669999998, 1000.9534, 1005.0838, 1001.57965);
        assertThat(trendValues).isNotNull().isInstanceOf(K6TrendValues.class);
    }
}