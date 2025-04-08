package com.helidon.adapter.out.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MetricsEntity(String id, String data, Instant timestamp,
                            List<MetricEntity> metricList) {
    public MetricsEntity(String data, Instant timestamp, List<MetricEntity> metricList) {
        this(UUID.randomUUID().toString(), data, timestamp, metricList);
    }
}
