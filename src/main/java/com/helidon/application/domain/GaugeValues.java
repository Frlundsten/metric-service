package com.helidon.application.domain;

import com.helidon.application.domain.model.Values;

public record GaugeValues(double value, double min, double max) implements Values {}
