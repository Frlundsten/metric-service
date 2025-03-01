package com.helidon.adapter.k6.domain.model;

import com.helidon.adapter.k6.domain.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Values;
import java.util.UUID;

public record K6Metric(String id, String name, K6Type type, Values values) implements Metric {
  public K6Metric(String name, K6Type type, Values values) {
    this(UUID.randomUUID().toString(), name, type, values);
  }
}
