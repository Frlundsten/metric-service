package com.helidon.adapter.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.helidon.adapter.application.k6.in.TrendValuesDTO;
import org.junit.jupiter.api.Test;

class K6TrendValuesTest {

  @Test
  void shouldCreateTrendValues() {
    var trendValues =
        new TrendValuesDTO(
            1005.6491, 1000.3286, 1001.3614669999998, 1000.9534, 1005.0838, 1001.57965);
    assertThat(trendValues).isNotNull().isInstanceOf(TrendValuesDTO.class);
  }
}
