package com.helidon.adapter.out.entity;

import static com.helidon.util.Mapper.toJson;

import com.helidon.application.domain.model.Metrics;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MetricsEntity(
    String id, String data, Instant timestamp, List<MetricEntity> metricList) {
  public MetricsEntity(String data, Instant timestamp, List<MetricEntity> metricList) {
    this(UUID.randomUUID().toString(), data, timestamp, metricList);
  }

  public static MetricsEntity fromDomain(Metrics metrics) {
    return new MetricsEntity(
        metrics.data(),
        metrics.timestamp(),
        metrics.metricList().stream()
            .map(
                metric ->
                    new MetricEntity(
                        metric.name().value(), metric.type().toString(), toJson(metric.values())))
            .toList());
  }
}
