package com.fl.adapter.in.rest.dto.response;

import com.fl.application.domain.model.Metric;
import com.fl.application.domain.model.Values;

public record MetricResponseDTO(String name, String type, Values values) {
    public static MetricResponseDTO from(Metric metric) {
        return new MetricResponseDTO(metric.name().value(), metric.type().name().toLowerCase(), metric.values());
    }
}