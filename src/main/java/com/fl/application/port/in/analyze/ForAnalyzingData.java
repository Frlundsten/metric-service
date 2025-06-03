package com.fl.application.port.in.analyze;

import com.fl.application.domain.model.MetricReport;

import java.util.List;

public interface ForAnalyzingData {
    String analyzeData(List<MetricReport> requests);
    String analyzeRecentRuns();
}
