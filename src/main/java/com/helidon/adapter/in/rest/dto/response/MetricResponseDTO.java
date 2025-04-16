package com.helidon.adapter.in.rest.dto.response;

import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Values;

public record MetricResponseDTO(String name, String type, Values values) {
    public static MetricResponseDTO from(Metric metric) {
        return new MetricResponseDTO(metric.name().value(), metric.type().name(), metric.values());
    }
}