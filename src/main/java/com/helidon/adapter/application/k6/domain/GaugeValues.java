package com.helidon.adapter.application.k6.domain;

import com.helidon.application.domain.model.Values;

public record GaugeValues(double value, double min, double max) implements Values {}
