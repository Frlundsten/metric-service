package com.helidon.adapter.application.domain.model;

import com.helidon.adapter.application.k6.domain.K6Type;
import org.junit.jupiter.api.Test;

import static com.helidon.adapter.application.k6.domain.K6Type.COUNTER;
import static com.helidon.adapter.application.k6.domain.K6Type.GAUGE;
import static com.helidon.adapter.application.k6.domain.K6Type.RATE;
import static com.helidon.adapter.application.k6.domain.K6Type.TIME;
import static org.assertj.core.api.Assertions.assertThat;

class K6TypeTest {

    @Test
    void shouldReturnType() {
        K6Type counter = COUNTER;
        K6Type trend = TIME;
        K6Type rate = RATE;
        K6Type gauge = GAUGE;

        assertThat(counter).isNotNull();
        assertThat(trend).isNotNull();
        assertThat(rate).isNotNull();
        assertThat(gauge).isNotNull();
    }
}