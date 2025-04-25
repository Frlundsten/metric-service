package com.helidon.adapter.in.rest;

import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;

public class DelegatingService implements HttpService {

  private final Handler metricsHandler;
  private final Handler getMetricsHandler;
  private final Handler recentReportsHandler;

  public DelegatingService(
      Handler metricsHandler, Handler getMetricsHandler, Handler recentReportsHandler) {
    this.metricsHandler = metricsHandler;
    this.getMetricsHandler = getMetricsHandler;
    this.recentReportsHandler = recentReportsHandler;
  }

  @Override
  public void routing(HttpRules rules) {
    rules.post(metricsHandler).get("/recent", recentReportsHandler).get(getMetricsHandler);
  }
}
