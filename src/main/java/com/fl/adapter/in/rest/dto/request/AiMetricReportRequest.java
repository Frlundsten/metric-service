package com.fl.adapter.in.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fl.application.domain.model.K6Type;
import com.fl.application.domain.model.Metric;
import com.fl.application.domain.model.MetricName;
import com.fl.application.domain.model.MetricReport;
import com.fl.application.domain.model.TrendValues;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public record AiMetricReportRequest(
        String id,
        Instant timestamp,
        List<AiMetricRequest> metrics
) {

    public MetricReport toDomain() {
        return new MetricReport(
                UUID.fromString(id()),
                "{}", timestamp(),
                metrics().stream().map(AiMetricRequest::toDomain).toList());
    }

    public record AiMetricRequest(
            String name,
            String type,
            AiTrendValues values
    ) {
        public Metric toDomain() {
            return new Metric(
                    new MetricName(name),
                    K6Type.valueOf(type().toUpperCase()),
                    values().toDomain()
            );
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiTrendValues(
            double max,
            double min,
            double avg,
            double med,
            double p95,
            double p90
    ) {
        public TrendValues toDomain() {
            return new TrendValues(
                    max(),
                    min(),
                    avg(),
                    med(),
                    p95(),
                    p90()
            );
        }
    }
}



