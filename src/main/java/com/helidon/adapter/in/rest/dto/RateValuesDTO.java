package com.helidon.adapter.in.rest.dto;

import com.helidon.application.domain.RateValues;

public record RateValuesDTO(double rate, double passes, double fails) implements ValuesDTO {
  public static RateValues toValues(RateValuesDTO values) {
    return new RateValues(values.rate(), values.passes(), values.fails());
  }
}
