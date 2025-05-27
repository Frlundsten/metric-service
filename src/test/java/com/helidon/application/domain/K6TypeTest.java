package com.helidon.application.domain;

import static com.fl.application.domain.model.K6Type.COUNTER;
import static com.fl.application.domain.model.K6Type.GAUGE;
import static com.fl.application.domain.model.K6Type.RATE;
import static com.fl.application.domain.model.K6Type.TREND;
import static org.assertj.core.api.Assertions.assertThat;

import com.fl.application.domain.model.K6Type;
import org.junit.jupiter.api.Test;

class K6TypeTest {

  @Test
  void shouldReturnType() {
    K6Type counter = COUNTER;
    K6Type trend = TREND;
    K6Type rate = RATE;
    K6Type gauge = GAUGE;

    assertThat(counter).isNotNull();
    assertThat(trend).isNotNull();
    assertThat(rate).isNotNull();
    assertThat(gauge).isNotNull();
  }
}
