package com.helidon.application.domain.model;

import com.helidon.exception.MetricNameValidationException;

public record MetricName(String value) {
  public MetricName {
    if (value == null || value.isBlank()) {
      throw new MetricNameValidationException("Metric name cannot be null or empty");
    }
  }
}
