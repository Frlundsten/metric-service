package com.fl.adapter.in.rest.dto.request;

import java.time.Instant;
import java.util.List;

public record AiMetricReportRequest(
        String id,
        Instant timestamp,
        List<AiMetricRequest> metrics
) {

    public record AiMetricRequest(
            String name,
            String type,
            AiTrendValues values
    ) {
    }
    public record AiTrendValues(
            double max,
            double min,
            double avg,
            double med,
            double p95,
            double p90
    ) {
    }
}



