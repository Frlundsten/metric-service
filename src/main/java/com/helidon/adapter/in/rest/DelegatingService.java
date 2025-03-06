package com.helidon.adapter.in.rest;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;

public class DelegatingService implements HttpService {

  private final CreateMetricsHandler metricsHandler;

  public DelegatingService(CreateMetricsHandler metricsHandler, GetMetricsHandler getMetricsHandler) {
    this.metricsHandler = metricsHandler;
  }

  @Override
  public void routing(HttpRules rules) {
    rules
        .post(metricsHandler)
        .get(
            (req, resp) -> {
              resp.status(200).send("Received 'GET' request");
            });
  }
}
