package com.fl.adapter.in.rest;

import com.fl.adapter.common.Mapper;
import com.fl.adapter.common.RepositoryId;
import com.fl.adapter.in.rest.dto.request.MetricReportRequestDTO;
import com.fl.application.port.in.create.ForCreateMetrics;
import com.fl.exception.AlertUserAdapterException;
import io.helidon.http.HeaderNames;
import io.helidon.http.media.jackson.JacksonRuntimeException;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateMetricsHandler implements Handler {
  public static final Logger LOG = LoggerFactory.getLogger(CreateMetricsHandler.class);
  private final ForCreateMetrics metricsCreation;
  private final Mapper mapper;

  public CreateMetricsHandler(
      ForCreateMetrics metricsCreation, Mapper mapper) {
    this.metricsCreation = metricsCreation;
    this.mapper = mapper;
  }

  @Override
  public void handle(ServerRequest req, ServerResponse res) {
    LOG.debug("Processing a post request..");
    try {
      var repoIdValue = req.headers().get(HeaderNames.create("Repository-Id"));
      RepositoryId repositoryId = new RepositoryId(repoIdValue.values());
      var dto = req.content().as(MetricReportRequestDTO.class);
      ScopedValue.where(RepositoryId.REPOSITORY_ID, repositoryId)
          .run(
              () -> {
                var response = handleRequest(dto);
                res.status(201).send(response);
              });
    } catch (NoSuchElementException e) {
      LOG.error("Missing header", e);
      res.status(400).send("No header");
    } catch (JacksonRuntimeException e) {
      LOG.error("Invalid request body", e);
      res.status(400).send("Invalid request body");
    }catch (AlertUserAdapterException e){
      res.status(201).send("Error during alerting user: " + e.getMessage());
    }
  }

  private MetricReportRequestDTO handleRequest(MetricReportRequestDTO dto) {
    metricsCreation.saveMetrics(mapper.toDomain(dto.metrics()));
    return dto;
  }
}
