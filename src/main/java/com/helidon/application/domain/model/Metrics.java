package com.helidon.application.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public record Metrics(String id, String data, Instant timestamp, List<Metric> metricList) {
  public Metrics(String data, List<Metric> metricList) {
    this(
        UUID.randomUUID().toString(),
        data,
        Instant.now().truncatedTo(ChronoUnit.SECONDS),
        metricList);
  }
}
