package com.helidon.adapter.in.rest.dto;

import com.helidon.application.domain.model.CounterValues;

public record CounterValuesDTO(double count, double rate) implements ValuesDTO {
  @Override
  public CounterValues toDomain() {
    return new CounterValues(count(), rate());
  }
}
