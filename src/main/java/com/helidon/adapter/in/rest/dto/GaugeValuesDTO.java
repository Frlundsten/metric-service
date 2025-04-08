package com.helidon.adapter.in.rest.dto;

import com.helidon.application.domain.model.GaugeValues;

public record GaugeValuesDTO(double value, double min, double max) implements ValuesDTO {
    @Override
    public GaugeValues toDomain() {
        return new GaugeValues(value(), min(), max());
    }
}
