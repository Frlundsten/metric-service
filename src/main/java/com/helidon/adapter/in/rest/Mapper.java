package com.helidon.adapter.in.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helidon.adapter.in.rest.dto.MetricDTO;
import com.helidon.application.domain.WantedK6Metrics;
import com.helidon.application.domain.model.K6Metric;
import com.helidon.application.domain.model.K6Metrics;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mapper {
  public static Logger LOG = LoggerFactory.getLogger(Mapper.class);
  ObjectMapper mapper = new ObjectMapper();

  public K6Metrics fromDtoMap(Map<String, MetricDTO> metrics) {
    try {
      var json = mapper.writeValueAsString(metrics);
      var listOfMetrics =
          metrics.entrySet().stream().map(this.createValidMetric).filter(Objects::nonNull).toList();
      LOG.debug("Created Metrics list: {}", listOfMetrics);
      return new K6Metrics(json, listOfMetrics);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not convert to JSON", e);
    }
  }

  protected Function<Map.Entry<String, MetricDTO>, K6Metric> createValidMetric =
      entry -> {
        if (WantedK6Metrics.isValid().test(entry.getKey().toUpperCase())) {
          return createMetric(entry.getKey(), entry.getValue());
        } else {
          return null;
        }
      };

  private K6Metric createMetric(String name, MetricDTO data) {
    //    var values =
    //        switch (data.type()) {
    //          case "rate" -> RateValuesDTO.toValues((RateValuesDTO) data.values());
    //          case "counter" -> CounterValuesDTO.toValues((CounterValuesDTO) data.values());
    //          case "trend" -> TrendValuesDTO.toValues((TrendValuesDTO) data.values());
    //          case "gauge" -> GaugeValuesDTO.toValues((GaugeValuesDTO) data.values());
    //          default -> throw new IllegalStateException("Unexpected type: " + data.type());
    //        };
    return K6Metric.from(name, data.type(), data.values().toDomain());
  }
}
