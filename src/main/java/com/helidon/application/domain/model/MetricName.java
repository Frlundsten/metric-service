package com.helidon.application.domain.model;

import com.helidon.application.domain.WantedK6Metrics;
import com.helidon.exception.MetricNameValidationException;

public record MetricName(String value) {
    public MetricName {
        if (value == null || value.isBlank()) {
            throw new MetricNameValidationException("Metric name cannot be null or empty");
        }
        try {
            WantedK6Metrics.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new MetricNameValidationException("Not a valid metric name " + value);
        }
    }
}
