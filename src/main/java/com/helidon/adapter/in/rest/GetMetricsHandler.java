package com.helidon.adapter.in.rest;

import com.helidon.adapter.in.rest.model.MetricsRequestDTO;
import com.helidon.application.port.in.manage.ForManagingMetrics;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetMetricsHandler implements Handler {
  public static Logger LOG = LoggerFactory.getLogger(CreateMetricsHandler.class);
  private final ForManagingMetrics forManagingMetrics;
  private final Mapper mapper;

  public GetMetricsHandler(ForManagingMetrics forManagingMetrics, Mapper mapper) {
    this.forManagingMetrics = forManagingMetrics;
    this.mapper = mapper;
  }

  @Override
  public void handle(ServerRequest req, ServerResponse res) {
    LOG.debug("Processing a get request..");
  }

  private MetricsRequestDTO handleRequest(String id) {
    return null;
  }
}
