package com.helidon.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.helidon.application.domain.model.CounterValues;
import org.junit.jupiter.api.Test;

class K6CounterValuesTest {

  @Test
  void shouldCreateK6CounterValues() {
    var counterValues = new CounterValues(8500, 848.8411196613823);
    assertThat(counterValues).isNotNull().isInstanceOf(CounterValues.class);
  }
}
