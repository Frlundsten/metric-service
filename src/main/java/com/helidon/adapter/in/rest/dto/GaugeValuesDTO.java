package com.helidon.adapter.in.rest.dto;

import com.helidon.application.domain.model.GaugeValues;
import com.helidon.application.domain.model.Values;

public record GaugeValuesDTO(double value, double min, double max) implements ValuesDTO {
  @Override
  public GaugeValues toDomain() {
    return new GaugeValues(value(), min(), max());
  }

  @Override
  public ValuesDTO toDTO(Values domain) {
    var values = (GaugeValues) domain;
    return new GaugeValuesDTO(values.value(), values.min(), values.max());
  }
}
