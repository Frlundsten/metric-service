package com.helidon.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helidon.application.domain.model.TrendValues;

public record TrendValuesDTO(
    double max,
    double min,
    double avg,
    double med,
    @JsonProperty("p(95)") double p95,
    @JsonProperty("p(90)") double p90)
    implements ValuesDTO {

  @Override
  public TrendValues toDomain() {
    return new TrendValues(max(), min(), avg(), med(), p95(), p90());
  }
}
