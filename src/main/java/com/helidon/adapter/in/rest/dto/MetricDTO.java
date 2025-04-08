package com.helidon.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public record MetricDTO(
    String type,
    String contains,
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type")
        @JsonSubTypes({
          @JsonSubTypes.Type(value = RateValuesDTO.class, name = "rate"),
          @JsonSubTypes.Type(value = CounterValuesDTO.class, name = "counter"),
          @JsonSubTypes.Type(value = TrendValuesDTO.class, name = "trend"),
          @JsonSubTypes.Type(value = GaugeValuesDTO.class, name = "gauge")
        })
        ValuesDTO values) {}
