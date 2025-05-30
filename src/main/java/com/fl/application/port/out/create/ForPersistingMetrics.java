package com.fl.application.port.out.create;

import com.fl.application.domain.model.MetricReport;

public interface ForPersistingMetrics {

    /**
     * Persist a report
     * @param metricReport
     */
    void saveMetrics(MetricReport metricReport);
}
