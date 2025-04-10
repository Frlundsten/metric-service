package com.helidon;

import com.helidon.adapter.out.entity.MetricEntity;

public class MetricFactory {

  private MetricFactory() {}

  public static MetricEntity createMetricEntity() {
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
    return new MetricEntity("HTTP_REQ_RECEIVING", "TREND", values);
  }
}
