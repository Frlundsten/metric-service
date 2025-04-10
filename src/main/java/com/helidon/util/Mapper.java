package com.helidon.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helidon.adapter.in.rest.dto.MetricDTO;
import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricsEntity;
import com.helidon.application.domain.WantedK6Metrics;
import com.helidon.application.domain.model.CounterValues;
import com.helidon.application.domain.model.GaugeValues;
import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.MetricName;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.domain.model.RateValues;
import com.helidon.application.domain.model.TrendValues;
import com.helidon.application.domain.model.Values;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mapper {
  public static final Logger LOG = LoggerFactory.getLogger(Mapper.class);
  static final ObjectMapper objectMapper = new ObjectMapper();

  public Metrics toDomain(Map<String, MetricDTO> metrics) {
    try {
      var json = toJson(metrics);
      var listOfMetrics =
          metrics.entrySet().stream().map(createValidMetric).filter(Objects::nonNull).toList();
      LOG.debug("Created Metrics list: {}", listOfMetrics);
      return new Metrics(json, listOfMetrics);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected Function<Map.Entry<String, MetricDTO>, Metric> createValidMetric =
      entry -> {
        if (WantedK6Metrics.isValid().test(entry.getKey().toUpperCase())) {
          return createMetric(entry.getKey(), entry.getValue());
        } else {
          return null;
        }
      };

  private Metric createMetric(String name, MetricDTO data) {
    return new Metric(
        new MetricName(name), K6Type.valueOf(data.type().toUpperCase()), data.values().toDomain());
  }

  public MetricsEntity toEntity(Metrics metrics) {
    return new MetricsEntity(
        metrics.data(),
        metrics.timestamp(),
        metrics.metricList().stream()
            .map(
                metric ->
                    new MetricEntity(
                        metric.name().value(), metric.type().toString(), toJson(metric.values())))
            .toList());
  }

  public Metric toDomain(MetricEntity metricEntity) {
    return new Metric(
        new MetricName(metricEntity.name()),
        K6Type.valueOf(metricEntity.type()),
        valueFromType(metricEntity.values(), metricEntity.type()));
  }

  private Values valueFromType(String values, String type) {
    try {
      return switch (type) {
        case "RATE" -> objectMapper.readValue(values, RateValues.class);
        case "TREND" -> objectMapper.readValue(values, TrendValues.class);
        case "GAUGE" -> objectMapper.readValue(values, GaugeValues.class);
        case "COUNTER" -> objectMapper.readValue(values, CounterValues.class);
        default -> null;
      };

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String toJson(Object values) {
    try {
      return objectMapper.writeValueAsString(values);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error serializing values to JSON", e);
    }
  }
}
