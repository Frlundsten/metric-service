package com.helidon.adapter.in.rest.dto;

import com.helidon.application.domain.model.Metrics;
import java.time.Instant;
import java.util.List;

public record MetricsResponseDTO(
    String id, Instant timestamp, List<MetricResponseDTO> metrics) {
  public static MetricsResponseDTO from(Metrics metrics) {
    List<MetricResponseDTO> dtoList =
        metrics.metricList().stream().map(MetricResponseDTO::from).toList();
    return new MetricsResponseDTO(metrics.id(), metrics.timestamp(), dtoList);
  }
}
