package com.helidon.adapter.in.rest;

import com.helidon.adapter.in.rest.model.MetricsRequestDTO;
import com.helidon.application.domain.RepositoryId;
import com.helidon.application.port.in.create.ForCreateMetrics;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ScopedValue;

public class CreateMetricsHandler implements Handler {
  public static Logger LOG = LoggerFactory.getLogger(CreateMetricsHandler.class);
  private final ForCreateMetrics metricsCreation;
  private final Mapper mapper;

  public CreateMetricsHandler(ForCreateMetrics metricsCreation, Mapper mapper) {
    this.metricsCreation = metricsCreation;
    this.mapper = mapper;
  }

  @Override
  public void handle(ServerRequest req, ServerResponse res) {
    LOG.debug("Processing a post request..");
    var repoIdValue = req.headers().get(HeaderNames.create("Repository-Id"));
    RepositoryId repositoryId = new RepositoryId(repoIdValue.values());
    var dto = req.content().as(MetricsRequestDTO.class);

    ScopedValue.where(RepositoryId.REPOSITORY_ID, repositoryId)
        .run(
            () -> {
              var response = handleRequest(dto);
              res.status(200).send(response);
            });
  }

  private MetricsRequestDTO handleRequest(MetricsRequestDTO dto) {
    K6Metrics metrics = mapper.fromDtoMap(dto.metrics());
    LOG.debug("Metrics object created: {}", metrics);
    metricsCreation.saveMetrics(metrics);
    return dto;
  }
}
