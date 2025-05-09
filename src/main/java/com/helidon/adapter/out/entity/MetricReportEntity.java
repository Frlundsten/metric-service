package com.helidon.adapter.out.entity;

import static com.helidon.adapter.common.Mapper.toJson;
import static com.helidon.adapter.common.Mapper.valueFromType;

import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.MetricName;
import com.helidon.application.domain.model.MetricReport;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MetricReportEntity(
    UUID id, String data, Instant timestamp, List<MetricEntity> metricList) {
  public MetricReportEntity(String data, Instant timestamp, List<MetricEntity> metricList) {
    this(UUID.randomUUID(), data, timestamp, metricList);
  }

  public static MetricReportEntity fromDomain(MetricReport metricReport) {
    return new MetricReportEntity(
        metricReport.id(),
        metricReport.data(),
        metricReport.timestamp(),
        metricReport.metricList().stream()
            .map(
                metric ->
                    new MetricEntity(
                        metric.name().value(), metric.type().toString(), toJson(metric.values())))
            .toList());
  }

  public static MetricReport toDomain(MetricReportEntity metricReportEntity) {
    return new MetricReport(
        metricReportEntity.id(),
        metricReportEntity.data(),
        metricReportEntity.timestamp(),
        metricReportEntity.metricList().stream()
            .map(
                metric ->
                    new Metric(
                        new MetricName(metric.name()),
                        K6Type.valueOf(metric.type().toUpperCase()),
                        valueFromType(metric.values(), metric.type().toUpperCase())))
            .toList());
  }
}
