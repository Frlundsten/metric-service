package com.helidon.adapter.application.k6.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class K6RateValuesTest {

  @Test
  void shouldCreateK6RateValues() {
    var rateValues = new RateValues(2, 100, 0);
    assertThat(rateValues).isNotNull().isInstanceOf(RateValues.class);
  }
}
