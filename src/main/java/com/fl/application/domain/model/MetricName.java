package com.fl.application.domain.model;

import com.fl.exception.MetricNameValidationException;

public record MetricName(String value) {
  public MetricName {
    if (value == null || value.isBlank()) {
      throw new MetricNameValidationException("Metric name cannot be null or empty");
    }
  }
}
