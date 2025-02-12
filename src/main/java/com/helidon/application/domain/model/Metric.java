package com.helidon.application.domain.model;

public interface Metric {
    String metricName();
    Type type();
    Values values();
}
