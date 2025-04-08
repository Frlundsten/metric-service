package com.helidon.application.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record Metrics(String data, Instant timestamp, List<Metric> metricList) {
    public Metrics(String data, List<Metric> metricList) {
        this(
                data,
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                metricList);
    }
}
