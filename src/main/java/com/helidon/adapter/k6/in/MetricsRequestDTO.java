package com.helidon.adapter.k6.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MetricsRequestDTO(Map<String, Object> metrics) {
}
