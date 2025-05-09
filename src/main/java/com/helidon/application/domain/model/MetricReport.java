package com.helidon.application.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public record MetricReport(UUID id, String data, Instant timestamp, List<Metric> metricList) {
  public MetricReport(String data, List<Metric> metricList) {
    this(UUID.randomUUID(), data, Instant.now().truncatedTo(ChronoUnit.SECONDS), metricList);
  }
}
