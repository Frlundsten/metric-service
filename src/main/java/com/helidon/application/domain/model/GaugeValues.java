package com.helidon.application.domain.model;

public record GaugeValues(double value, double min, double max) implements Values {}
