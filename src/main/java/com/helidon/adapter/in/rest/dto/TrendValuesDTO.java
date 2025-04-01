package com.helidon.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helidon.application.domain.TrendValues;

public record TrendValuesDTO(
    double max,
    double min,
    double avg,
    double med,
    @JsonProperty("p(95)") double p95,
    @JsonProperty("p(90)") double p90)
    implements ValuesDTO {
  public static TrendValues toValues(TrendValuesDTO dto) {
    return new TrendValues(dto.max(), dto.min(), dto.avg(), dto.med(), dto.p95(), dto.p90());
  }
}
