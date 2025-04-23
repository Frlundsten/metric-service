package com.helidon.application.port.out.create;

import com.helidon.application.domain.model.MetricReport;

public interface ForPersistingMetrics {
    void saveMetrics(MetricReport metricReport);
}
