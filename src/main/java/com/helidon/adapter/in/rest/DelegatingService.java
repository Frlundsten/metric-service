package com.helidon.adapter.in.rest;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;

public class DelegatingService implements HttpService {

  private final CreateMetricsHandler metricsHandler;
  private final ReportTimespanHandler getMetricsHandler;
  private final RecentReportsHandler recentReportsHandler;

  public DelegatingService(
          CreateMetricsHandler metricsHandler, ReportTimespanHandler getMetricsHandler, RecentReportsHandler recentReportsHandler) {
    this.metricsHandler = metricsHandler;
    this.getMetricsHandler = getMetricsHandler;
    this.recentReportsHandler = recentReportsHandler;
  }

  @Override
  public void routing(HttpRules rules) {
    rules.post(metricsHandler).get("/recent", recentReportsHandler).get(getMetricsHandler);
  }
}
