package com.helidon.adapter.application.k6.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helidon.application.domain.model.Values;

public record TrendValuesDTO(
    double max,
    double min,
    double avg,
    double med,
    @JsonProperty("p(95)") double p95,
    @JsonProperty("p(90)") double p90)
    implements Values {}
