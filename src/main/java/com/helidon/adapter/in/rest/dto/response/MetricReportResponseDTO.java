package com.helidon.adapter.in.rest.dto.response;

import com.helidon.application.domain.model.MetricReport;
import java.util.List;

public record MetricReportResponseDTO(
    String id, String timestamp, List<MetricResponseDTO> metrics) {
  public static MetricReportResponseDTO from(MetricReport metricReport) {
    List<MetricResponseDTO> dtoList =
        metricReport.metricList().stream().map(MetricResponseDTO::from).toList();
    return new MetricReportResponseDTO(
        metricReport.id().toString(), String.valueOf(metricReport.timestamp()), dtoList);
  }
}
