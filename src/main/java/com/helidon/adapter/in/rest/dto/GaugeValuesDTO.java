package com.helidon.adapter.in.rest.dto;

import com.helidon.application.domain.GaugeValues;

public record GaugeValuesDTO(double value, double min, double max) implements ValuesDTO {
  public static GaugeValues toValues(GaugeValuesDTO values) {
    return new GaugeValues(values.value(), values.min(), values.max());
  }
}
