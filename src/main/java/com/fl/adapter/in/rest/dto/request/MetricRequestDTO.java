package com.fl.adapter.in.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fl.adapter.in.rest.dto.CounterValuesDTO;
import com.fl.adapter.in.rest.dto.GaugeValuesDTO;
import com.fl.adapter.in.rest.dto.RateValuesDTO;
import com.fl.adapter.in.rest.dto.TrendValuesDTO;
import com.fl.adapter.in.rest.dto.ValuesDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MetricRequestDTO(
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
