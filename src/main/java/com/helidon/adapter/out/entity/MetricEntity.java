package com.helidon.adapter.out.entity;

import java.util.UUID;

public record MetricEntity(String id, String name, String type, String values) {
    public MetricEntity(String name, String type, String values) {
        this(UUID.randomUUID().toString(), name, type, values);
    }
}
