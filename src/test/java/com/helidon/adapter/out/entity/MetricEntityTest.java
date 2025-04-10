package com.helidon.adapter.out.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MetricEntityTest {

  @Test
  void testMetricEntity() {
    String values =
        """
                {
                    "avg": 0.02642,
                    "min": 0,
                    "med": 0,
                    "max": 0.5284,
                    "p(90)": 0,
                    "p(95)": 0.026419999999998497
                }
                """;

    MetricEntity entity = new MetricEntity("HTTP_REQ_RECEIVING", "TREND", values);
    assertThat(entity).isNotNull();
    assertThat(entity.id()).isNotNull();
    assertThat(entity.name()).isEqualTo("HTTP_REQ_RECEIVING");
    assertThat(entity.type()).isEqualTo("TREND");
    assertThat(entity.values()).isNotNull();
  }
}
