package com.fl.adapter.out.ai;

import com.fl.application.domain.model.Metric;
import com.fl.application.domain.model.TrendValues;

public record AiMetricResource(
        String name,
        String type,
        double max,
        double min,
        double avg,
        double med,
        double p95,
        double p90) {

    public static AiMetricResource toResource(Metric metric) {
        var vals = (TrendValues) metric.values();
        return new AiMetricResource(
                metric.name().value(),
                metric.type().name(),
                vals.max(),
                vals.min(),
                vals.avg(),
                vals.med(),
                vals.p95(),
                vals.p90()
        );
    }
}
