package com.fl.adapter.in.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MetricReportRequestDTO(Map<String, MetricRequestDTO> metrics) {
}
