package com.helidon.adapter.in.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MetricsRequestDTO(Map<String, MetricRequestDTO> metrics) {
}
