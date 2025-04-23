package com.helidon;

import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricReportEntity;
import com.helidon.application.domain.model.CounterValues;
import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.MetricName;
import com.helidon.application.domain.model.MetricReport;
import com.helidon.application.domain.model.Values;
import java.time.Instant;
import java.util.List;

public class MetricFactory {

  private MetricFactory() {}

  public static MetricEntity createMetricEntity() {
    return new MetricEntity("HTTP_REQ_RECEIVING", "TREND", getValues());
  }

  public static MetricReportEntity createMetricReportEntityWithList() {
    return new MetricReportEntity("{\"key\":\"val\"}", Instant.now(), List.of(createMetricEntity()));
  }

  public static MetricReportEntity createMetricReportEntityWithEmptyList() {
    return new MetricReportEntity("{\"key\":\"val\"}", Instant.now(), List.of());
  }

  public static MetricReport createMetricReportWithEmptyList() {
    return new MetricReport("{}", List.of());
  }

  public static MetricReport createMetricReport() {
    return new MetricReport(
            "{}", List.of(new Metric(new MetricName("key"), K6Type.RATE, getCounterValues())));
  }

  public static MetricReport createMetricReport(String metricName) {
    return new MetricReport(
            "{}", List.of(new Metric(new MetricName(metricName), K6Type.RATE, getCounterValues())));
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
