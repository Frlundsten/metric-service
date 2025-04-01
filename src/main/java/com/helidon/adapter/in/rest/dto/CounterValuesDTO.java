package com.helidon.adapter.in.rest.dto;

import com.helidon.application.domain.CounterValues;

public record CounterValuesDTO(double count, double rate) implements ValuesDTO {
  public static CounterValues toValues(CounterValuesDTO values) {
    return new CounterValues(values.count(), values.rate());
  }

  @Override
  public ValuesDTO getValues() {
    return this;
  }
}
