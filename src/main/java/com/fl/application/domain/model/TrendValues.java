package com.fl.application.domain.model;

public record TrendValues(double max, double min, double avg, double med, double p95, double p90)
        implements Values {
}

