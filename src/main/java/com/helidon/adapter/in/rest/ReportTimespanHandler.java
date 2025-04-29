package com.helidon.adapter.in.rest;

import com.helidon.adapter.RepositoryId;
import com.helidon.adapter.in.rest.dto.response.MetricReportResponseDTO;
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

public class ReportTimespanHandler implements Handler {
  public static final Logger LOG = LoggerFactory.getLogger(ReportTimespanHandler.class);
  private final ForManagingMetrics manage;

  public ReportTimespanHandler(ForManagingMetrics forManagingMetrics) {
    this.manage = forManagingMetrics;
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
                var response = handleRequest(fromDate, toDate);
                res.status(200).send(response);
              } catch (NoSuchElementException e) {
                res.status(400).send(e);
              }
            });
  }

  private List<MetricReportResponseDTO> handleRequest(String fromDate, String toDate) {
    LOG.debug("Get metrics created between {} and {}", fromDate, toDate);
    var from = Instant.parse(fromDate.trim());
    var to = Instant.parse(toDate.trim());
    var metricsList = manage.getBetweenDates(from, to);
    return metricsList.stream().map(MetricReportResponseDTO::from).toList();
  }
}
