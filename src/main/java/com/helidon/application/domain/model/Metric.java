package com.helidon.application.domain.model;

public interface Metric {
    String name();
    Type type();
    Values values();
}
