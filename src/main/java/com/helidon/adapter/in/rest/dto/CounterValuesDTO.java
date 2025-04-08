package com.helidon.adapter.in.rest.dto;

import com.helidon.application.domain.CounterMetric;
import com.helidon.application.domain.CounterValues;

public record CounterValuesDTO(double count, double rate) implements ValuesDTO {
  public static CounterValues toValues(CounterValuesDTO values) {
    return new CounterValues(values.count(), values.rate());
  }

  @Override
  public CounterMetric.CounterValues toDomain() {
    return new CounterMetric.CounterValues(count(), rate());
  }
}
