package com.helidon.adapter.out.entity;

import static com.helidon.util.Mapper.valueFromType;

import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.MetricName;
import java.util.UUID;

public record MetricEntity(String id, String name, String metricsId, String type, String values) {
  public MetricEntity(String name, String type, String values) {
    this(UUID.randomUUID().toString(), name,null, type, values);
  }

  public static Metric toDomain(MetricEntity metricEntity) {
    return new Metric(
        new MetricName(metricEntity.name()),
        K6Type.valueOf(metricEntity.type()),
        valueFromType(metricEntity.values(), metricEntity.type()));
  }
}
