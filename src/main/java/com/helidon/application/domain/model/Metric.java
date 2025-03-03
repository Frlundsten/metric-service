package com.helidon.application.domain.model;

public interface Metric {
    String id();
    String name();
    Type type();
    Values values();
}
