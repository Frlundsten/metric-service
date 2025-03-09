package com.helidon.adapter.in.rest;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;

public class DelegatingService implements HttpService {

  private final CreateMetricsHandler metricsHandler;
  private final GetMetricsHandler getMetricsHandler;

  public DelegatingService(
      CreateMetricsHandler metricsHandler, GetMetricsHandler getMetricsHandler) {
    this.metricsHandler = metricsHandler;
    this.getMetricsHandler = getMetricsHandler;
  }

  @Override
  public void routing(HttpRules rules) {
    rules.post(metricsHandler).get(getMetricsHandler);
  }
}
