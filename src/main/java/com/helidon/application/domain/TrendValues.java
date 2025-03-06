package com.helidon.application.domain;

import com.helidon.application.domain.model.Values;

public record TrendValues(double max, double min, double avg, double med, double p95, double p90)
    implements Values {}
