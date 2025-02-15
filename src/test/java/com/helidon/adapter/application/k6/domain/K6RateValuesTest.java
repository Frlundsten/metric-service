package com.helidon.adapter.application.k6.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class K6RateValuesTest {

    @Test
    void shouldCreateK6RateValues() {
        var rateValues = new K6RateValues(2, 100, 0);
        assertThat(rateValues).isNotNull().isInstanceOf(K6RateValues.class);
    }

}