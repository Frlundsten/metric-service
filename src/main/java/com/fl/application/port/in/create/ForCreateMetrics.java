package com.fl.application.port.in.create;

import com.fl.application.domain.model.MetricReport;

public interface ForCreateMetrics {
    void saveMetrics(MetricReport metricReport);
}
