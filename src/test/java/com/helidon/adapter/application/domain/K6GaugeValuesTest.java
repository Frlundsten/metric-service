package com.helidon.adapter.application.domain;

import com.helidon.adapter.application.k6.domain.K6GaugeValues;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class K6GaugeValuesTest {

    @Test
    void shouldCreateK6GaugeValues() {
        var gaugeValues = new K6GaugeValues(10, 10, 10);
        assertThat(gaugeValues).isNotNull().isInstanceOf(K6GaugeValues.class);
    }

}