package com.helidon.adapter.k6.domain;

import com.helidon.application.domain.model.Values;

public record RateValues(double rate, double passes, double fails) implements Values {}
