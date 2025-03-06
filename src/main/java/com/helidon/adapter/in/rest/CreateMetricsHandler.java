package com.helidon.adapter.in.rest;

import com.helidon.adapter.in.rest.model.MetricsRequestDTO;
import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.in.create.ForCreateMetrics;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateMetricsHandler implements Handler {
  public static Logger LOG = LoggerFactory.getLogger(CreateMetricsHandler.class);
  private final ForCreateMetrics metricsCreation;
  private final Mapper mapper;

  public CreateMetricsHandler(
      ForCreateMetrics metricsCreation, Mapper mapper) {
    this.metricsCreation = metricsCreation;
    this.mapper = mapper;
  }

  @Override
  public void handle(ServerRequest req, ServerResponse res) {
    LOG.info("Processing a post request..");
    try {
      var headerValue = req.headers().get(HeaderNames.create("repository-id"));
      var id = headerValue.get();

      var reqBody = req.content().as(MetricsRequestDTO.class);

      var response = handleRequest(reqBody);
      res.status(200).send(response);
    } catch (NoSuchElementException e) {
      res.status(400).send("Header 'repository-id' not found");
    }
  }

  private MetricsRequestDTO handleRequest(MetricsRequestDTO reqBody) {
    Metrics metrics = mapper.fromDTO(reqBody.metrics());
    metricsCreation.saveMetrics(metrics);
    return reqBody;
  }
}
