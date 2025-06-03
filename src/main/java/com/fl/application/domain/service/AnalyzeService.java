package com.fl.application.domain.service;

import com.fl.application.domain.model.MetricReport;
import com.fl.application.port.in.analyze.ForAnalyzingData;
import com.fl.application.port.out.analyze.ForDataAnalysis;
import com.fl.application.port.out.manage.ForManagingStoredMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeService implements ForAnalyzingData {

    public static final Logger LOG = LoggerFactory.getLogger(AnalyzeService.class);

    private final ForDataAnalysis forDataAnalysis;
    private final ForManagingStoredMetrics manageStoredMetrics;

    public AnalyzeService(ForDataAnalysis forDataAnalysis, ForManagingStoredMetrics manageStoredMetrics) {
        this.forDataAnalysis = forDataAnalysis;
        this.manageStoredMetrics = manageStoredMetrics;
    }

    @Override
    public String analyzeData(List<MetricReport> requests) {
       return forDataAnalysis.analyzeData(requests);
    }

    @Override
    public String analyzeRecentRuns() {
        LOG.debug("Analyzing recent runs");
        var recentRuns = manageStoredMetrics.getRecentFromView();
       return forDataAnalysis.analyzeRecent(getMostRecent(recentRuns));
    }

    /**
     * Get the 5 most recent k6 run summaries to analyze.
     * @param recentRuns Fetched from the 30 day view
     * @return At most 5 MetricReports
     */
    private List<MetricReport> getMostRecent(List<MetricReport> recentRuns) {
        if (recentRuns.size() <= 5) {
            return new ArrayList<>(recentRuns);
        } else {
            return recentRuns.subList(recentRuns.size() - 5, recentRuns.size());
        }
    }
}
