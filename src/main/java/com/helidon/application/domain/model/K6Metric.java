package com.helidon.application.domain.model;

import java.util.UUID;

public record K6Metric(String id, String name, K6Type type, Values values) implements Metric {
  public K6Metric(String name, K6Type type, Values values) {
    this(UUID.randomUUID().toString(), name, type, values);
  }

  public static K6Metric from(String name, String type, Values values) {
    return new K6Metric(name, K6Type.valueOf(type.toUpperCase()), values);
  }
}
