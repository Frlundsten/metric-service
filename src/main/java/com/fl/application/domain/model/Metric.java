package com.fl.application.domain.model;

public record Metric(MetricName name, K6Type type, Values values) {}
