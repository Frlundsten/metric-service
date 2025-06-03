package com.fl.adapter.in.rest;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;

public class DelegatingService implements HttpService {

    private final CreateMetricsHandler metricsHandler;
    private final ReportTimespanHandler getMetricsHandler;
    private final RecentReportsHandler recentReportsHandler;
    private final AnalyzeHandler analyzeHandler;

    public DelegatingService(
            CreateMetricsHandler metricsHandler,
            ReportTimespanHandler getMetricsHandler,
            RecentReportsHandler recentReportsHandler,
            AnalyzeHandler analyzeHandler) {
        this.metricsHandler = metricsHandler;
        this.getMetricsHandler = getMetricsHandler;
        this.recentReportsHandler = recentReportsHandler;
        this.analyzeHandler = analyzeHandler;
    }

    @Override
    public void routing(HttpRules rules) {
        rules
                .post("/analyze", analyzeHandler)
                .post(metricsHandler)
                .get("/recent", recentReportsHandler)
                .get(getMetricsHandler);
    }
}
