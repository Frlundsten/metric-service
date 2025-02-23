package com.helidon.adapter.k6.in;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helidon.adapter.k6.domain.CounterValues;
import com.helidon.adapter.k6.domain.GaugeValues;
import com.helidon.adapter.k6.domain.K6Type;
import com.helidon.adapter.k6.domain.RateValues;
import com.helidon.adapter.k6.domain.TrendValues;
import com.helidon.adapter.k6.domain.WantedK6Metrics;
import com.helidon.adapter.k6.domain.model.K6Metric;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Metrics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mapper {
  public static Logger log = LoggerFactory.getLogger(Mapper.class);
  ObjectMapper mapper = new ObjectMapper();

  public Metrics fromDTO(Map<String, Object> dto) {
    String json;
    List<Metric> metricList = new ArrayList<>();

    /*
       Hold the reference as JSON string to put in metrics object.
    */
    try {
      json = mapper.writeValueAsString(dto);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not convert to JSON", e);
    }

    /*
      For each metric name in the map, check if it matches a wanted metric names.
    */
    dto.forEach(
        (metricName, obj) -> {
          if (Arrays.stream(WantedK6Metrics.values())
              .anyMatch(value -> value.name().equalsIgnoreCase(metricName))) {

            // Convert the object of the key to a JsonNode
            var node = mapper.convertValue(obj, JsonNode.class);

            // Grab the type from the node and use it to create the correct Type enum instance
            var type = node.get("type").asText().toUpperCase();
            var typeImpl = mapper.convertValue(type, K6Type.class);

            // Grab the values from the node and create the correct value implementation based of
            // the type
            var values = node.get("values");
            var valuesImpl =
                switch (typeImpl) {
                  case RATE -> mapper.convertValue(values, RateValues.class);
                  case COUNTER -> mapper.convertValue(values, CounterValues.class);
                  case TREND -> mapper.convertValue(values, TrendValuesDTO.class);
                  case GAUGE -> mapper.convertValue(values, GaugeValues.class);
                };

            if (valuesImpl
                instanceof
                TrendValuesDTO(
                    double max,
                    double min,
                    double avg,
                    double med,
                    double p95,
                    double p90)) {
              valuesImpl = new TrendValues(max, min, avg, med, p95, p90);
            }

            var metric = new K6Metric(metricName, typeImpl, valuesImpl);
            metricList.add(metric);
          }
        });
    return new Metrics(json, metricList);
  }
}
