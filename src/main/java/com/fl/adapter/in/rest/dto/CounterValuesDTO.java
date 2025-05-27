package com.fl.adapter.in.rest.dto;

import com.fl.application.domain.model.CounterValues;
import com.fl.application.domain.model.Values;

public record CounterValuesDTO(double count, double rate) implements ValuesDTO {
  @Override
  public CounterValues toDomain() {
    return new CounterValues(count(), rate());
  }

  @Override
  public ValuesDTO toDTO(Values domain) {
    var values = (CounterValues) domain;
    return new CounterValuesDTO(values.count(), values.rate());
  }
}
