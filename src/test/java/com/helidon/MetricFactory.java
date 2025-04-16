package com.helidon;

import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricsEntity;
import com.helidon.application.domain.model.CounterValues;
import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.MetricName;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.domain.model.Values;
import java.time.Instant;
import java.util.List;

public class MetricFactory {

  private MetricFactory() {}

  public static MetricEntity createMetricEntity() {
    return new MetricEntity("HTTP_REQ_RECEIVING", "TREND", getValues());
  }

  public static MetricsEntity createMetricsEntityWithList() {
    return new MetricsEntity("{\"key\":\"val\"}", Instant.now(), List.of(createMetricEntity()));
  }

  public static MetricsEntity createMetricsEntityWithEmptyList() {
    return new MetricsEntity("{\"key\":\"val\"}", Instant.now(), List.of());
  }

  public static Metrics createMetricsWithEmptyList() {
    return new Metrics("{}", List.of());
  }

  public static Metrics createMetricsWithList() {
    return new Metrics(
        "{}", List.of(new Metric(new MetricName("key"), K6Type.RATE, getCounterValues())));
  }

  private static String getValues() {
    return """
                    {
                        "avg": 0.02642,
                        "min": 0,
                        "med": 0,
                        "max": 0.5284,
                        "p(90)": 0,
                        "p(95)": 0.026419999999998497
                    }
                    """;
  }

  private static Values getCounterValues() {
    return new CounterValues(1, 2);
  }
}
