package com.helidon.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helidon.adapter.in.rest.WantedK6Metrics;
import com.helidon.adapter.in.rest.dto.request.MetricRequestDTO;
import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.MetricName;
import com.helidon.application.domain.model.MetricReport;
import com.helidon.application.domain.model.Values;
import com.helidon.exception.EmptyMetricListException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mapper {
  public static final Logger LOG = LoggerFactory.getLogger(Mapper.class);
  static final ObjectMapper objectMapper = ObjectMapperFactory.create();

  public MetricReport toDomain(Map<String, MetricRequestDTO> metrics) {
    String json = toJson(metrics);

    List<Metric> validMetrics =
        metrics.entrySet().stream().filter(this::isWantedMetric).map(this::toMetric).toList();

    if (validMetrics.isEmpty()) {
      throw new EmptyMetricListException("No metric data found");
    }

    LOG.debug("Created MetricReport list: {}", validMetrics);
    return new MetricReport(json, validMetrics);
  }

  private boolean isWantedMetric(Map.Entry<String, MetricRequestDTO> entry) {
    return WantedK6Metrics.isValid().test(entry.getKey().toUpperCase());
  }

  private Metric toMetric(Map.Entry<String, MetricRequestDTO> entry) {
    return createMetric(entry.getKey(), entry.getValue());
  }

  protected Metric createMetric(String name, MetricRequestDTO dto) {
    return new Metric(
        new MetricName(name), K6Type.valueOf(dto.type().toUpperCase()), dto.values().toDomain());
  }

  public static Values valueFromType(String values, String typeStr) {
    try {
      K6Type type = K6Type.valueOf(typeStr.toUpperCase());
      return objectMapper.readValue(values, type.getValueClass());
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("Invalid K6Type: " + typeStr, e);
    } catch (IOException e) {
      throw new RuntimeException("Failed to deserialize values for type: " + typeStr, e);
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
