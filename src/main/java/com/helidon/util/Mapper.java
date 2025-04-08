package com.helidon.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helidon.adapter.in.rest.dto.MetricDTO;
import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricsEntity;
import com.helidon.application.domain.WantedK6Metrics;
import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.MetricName;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.domain.model.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Mapper {
    public static Logger LOG = LoggerFactory.getLogger(Mapper.class);
    ObjectMapper objectMapper = new ObjectMapper();

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
        return new Metric(new MetricName(name), K6Type.valueOf(data.type().toUpperCase()), data.values().toDomain());
    }

    public MetricsEntity toEntity(Metrics metrics) {
        return new MetricsEntity(
                metrics.data(), metrics.timestamp(),
                metrics.metricList().stream().map(metric ->
                        new MetricEntity(metric.name().value(), metric.type().toString(), toJson(metric.values()))
                ).toList()
        );
    }

    public static String toJson(Object values) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(values);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing values to JSON", e);
        }
    }


}
