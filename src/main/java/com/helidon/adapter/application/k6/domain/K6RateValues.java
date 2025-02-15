package com.helidon.adapter.application.k6.domain;

import com.helidon.application.domain.model.Values;

public record K6RateValues(double rate, double passes, double fails) implements Values {
}
