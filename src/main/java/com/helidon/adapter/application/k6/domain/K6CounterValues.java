package com.helidon.adapter.application.k6.domain;

import com.helidon.application.domain.model.Values;

public record K6CounterValues(double count, double rate) implements Values {
}
