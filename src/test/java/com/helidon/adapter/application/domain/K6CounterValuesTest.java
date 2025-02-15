package com.helidon.adapter.application.domain;

import com.helidon.adapter.application.k6.domain.K6CounterValues;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class K6CounterValuesTest {

    @Test
    void shouldCreateK6CounterValues() {
        var counterValues = new K6CounterValues(8500, 848.8411196613823);
        assertThat(counterValues).isNotNull().isInstanceOf(K6CounterValues.class);
    }
}