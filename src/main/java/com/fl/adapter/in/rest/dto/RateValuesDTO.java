package com.fl.adapter.in.rest.dto;

import com.fl.application.domain.model.RateValues;
import com.fl.application.domain.model.Values;

public record RateValuesDTO(double rate, double passes, double fails) implements ValuesDTO {
  @Override
  public RateValues toDomain() {
    return new RateValues(rate(), passes(), fails());
  }

  @Override
  public ValuesDTO toDTO(Values domain) {
    var values = (RateValues) domain;
    return new RateValuesDTO(values.rate(), values.passes(), values.fails());
  }
}
