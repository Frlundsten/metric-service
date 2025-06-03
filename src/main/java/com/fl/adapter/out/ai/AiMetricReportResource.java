package com.fl.adapter.out.ai;

import com.fl.application.domain.model.MetricReport;

import java.time.Instant;
import java.util.List;

public record AiMetricReportResource(
        String id,
        Instant timestamp,
        List<AiMetricResource> metricList
) {
    public static AiMetricReportResource toAiResource(MetricReport report) {
        return new AiMetricReportResource(
                report.id().toString(),
                report.timestamp(),
                report.metricList()
                        .stream()
                        .map(AiMetricResource::toResource)
                        .toList());
    }

}
