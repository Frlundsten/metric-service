package com.helidon.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MetricsRequestDTO(Map<String, MetricDTO> metrics) {
}
