package com.helidon.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.helidon.application.domain.model.GaugeValues;
import org.junit.jupiter.api.Test;

class K6GaugeValuesTest {

  @Test
  void shouldCreateK6GaugeValues() {
    var gaugeValues = new GaugeValues(10, 10, 10);
    assertThat(gaugeValues).isNotNull().isInstanceOf(GaugeValues.class);
  }
}
