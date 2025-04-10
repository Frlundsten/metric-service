package com.helidon.adapter.in.rest;

import com.helidon.adapter.in.rest.dto.MetricsResponseDTO;
import com.helidon.application.RepositoryId;
import com.helidon.application.port.in.manage.ForManagingMetrics;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetMetricsHandler implements Handler {
  public static Logger LOG = LoggerFactory.getLogger(GetMetricsHandler.class);
  private final ForManagingMetrics forManagingMetrics;

  public GetMetricsHandler(ForManagingMetrics forManagingMetrics) {
    this.forManagingMetrics = forManagingMetrics;
  }

  @Override
  public void handle(ServerRequest req, ServerResponse res) {
    LOG.debug("Processing a GET request..");
    var repoIdValue = req.headers().get(HeaderNames.create("Repository-Id"));
    RepositoryId repositoryId = new RepositoryId(repoIdValue.values());
    ScopedValue.where(RepositoryId.REPOSITORY_ID, repositoryId)
        .run(
            () -> {
              try {
                var fromDate = req.query().get("from");
                var toDate = req.query().get("to");
                LOG.debug("Processing a GET request..WRFGFAWEFFSFS");
                var response = handleRequest(fromDate, toDate);
                res.status(200).send(response);
              } catch (NoSuchElementException e) {
                res.status(400).send(e);
              }
            });
  }

  private List<MetricsResponseDTO> handleRequest(String fromDate, String toDate) {
    LOG.debug("Get metrics created between {} and {}", fromDate, toDate);
    var from = Instant.parse(fromDate);
    var to = Instant.parse(toDate);
    var metricsList = forManagingMetrics.getBetweenDates(from, to);
    return metricsList.stream().map(MetricsResponseDTO::from).toList();
  }
}
