package com.helidon.adapter.in.rest.dto;

import com.helidon.application.domain.model.RateValues;

public record RateValuesDTO(double rate, double passes, double fails) implements ValuesDTO {
    @Override
    public RateValues toDomain() {
        return new RateValues(rate(), passes(), fails());
    }
}
