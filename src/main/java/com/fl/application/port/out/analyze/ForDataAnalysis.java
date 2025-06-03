package com.fl.application.port.out.analyze;

import com.fl.application.domain.model.MetricReport;

import java.util.List;

public interface ForDataAnalysis {
    String analyzeData(List<MetricReport> requests);

    String analyzeRecent(List<MetricReport> requests);
}
