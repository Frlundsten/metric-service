package com.helidon.application.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public record K6Metrics(String id, String data, Instant timestamp, List<K6Metric> metricList) {
  public K6Metrics(String data, List<K6Metric> metricList) {
    this(
        UUID.randomUUID().toString(),
        data,
        Instant.now().truncatedTo(ChronoUnit.SECONDS),
        metricList);
  }
}
