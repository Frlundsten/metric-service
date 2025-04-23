package com.helidon.application.port.in.create;

import com.helidon.application.domain.model.MetricReport;

public interface ForCreateMetrics {
    void saveMetrics(MetricReport metricReport);
}
