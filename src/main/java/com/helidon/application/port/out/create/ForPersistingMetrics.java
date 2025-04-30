package com.helidon.application.port.out.create;

import com.helidon.application.domain.model.MetricReport;

public interface ForPersistingMetrics {

    /**
     * Persist a report
     * @param metricReport
     */
    void saveMetrics(MetricReport metricReport);
}
