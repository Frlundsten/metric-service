package com.helidon.adapter.application.k6.domain;

import com.helidon.application.domain.model.Values;

public record K6TrendValues(double max, double min, double avg, double med, double p95, double p90) implements Values {
}
