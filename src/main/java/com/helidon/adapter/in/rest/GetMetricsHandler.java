package com.helidon.adapter.in.rest;

import com.helidon.adapter.in.rest.model.MetricsRequestDTO;
import com.helidon.application.port.in.manage.ForManagingMetrics;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
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
    LOG.debug("Processing a GET request..");
    try {
      var headerValue = req.headers().get(HeaderNames.create("repository-id"));
      var id = headerValue.get();

      Map<String, Object> request = req.content().as(Map.class);

      var fromDate = request.get("from");
      var toDate = request.get("to");
      if (fromDate != null) {
        LOG.debug("Found from body: {}", fromDate);

        var from = Instant.parse((CharSequence) fromDate);
        var to = Instant.parse((CharSequence) toDate);

        var response = forManagingMetrics.getBetweenDates(from, to);

        res.status(200).send(response);
      }
    } catch (NoSuchElementException e) {
      res.status(400).send("Header 'repository-id' not found");
    }
  }

  private MetricsRequestDTO handleRequest(String id) {
    return null;
  }
}
