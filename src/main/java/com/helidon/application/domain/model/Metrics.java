package com.helidon.application.domain.model;

import java.time.Instant;
import java.util.List;

public record Metrics(String data, Instant timestamp , List<Metric> metricList) {
}
