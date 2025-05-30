package com.fl.adapter.out.persistence.entity;

import static com.fl.adapter.common.Mapper.valueFromType;

import com.fl.application.domain.model.K6Type;
import com.fl.application.domain.model.Metric;
import com.fl.application.domain.model.MetricName;
import java.util.UUID;

public record MetricEntity(UUID id, String name, UUID metricsId, String type, String values) {
  public MetricEntity(String name, String type, String values) {
    this(UUID.randomUUID(), name,null, type, values);
  }

  public static Metric toDomain(MetricEntity metricEntity) {
    return new Metric(
        new MetricName(metricEntity.name()),
        K6Type.valueOf(metricEntity.type()),
        valueFromType(metricEntity.values(), metricEntity.type()));
  }
}
