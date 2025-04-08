package com.helidon.adapter.in.rest.dto;

import com.helidon.application.domain.GaugeMetric;
import com.helidon.application.domain.GaugeValues;

public record GaugeValuesDTO(double value, double min, double max) implements ValuesDTO {
  public static GaugeValues toValues(GaugeValuesDTO values) {
    return new GaugeValues(values.value(), values.min(), values.max());
  }

  @Override
  public GaugeMetric.GaugeValues toDomain() {
    return new GaugeMetric.GaugeValues(value(), min(), max());
  }
}
