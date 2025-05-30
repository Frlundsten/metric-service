package com.fl.adapter.in.rest;

import com.fl.adapter.common.RepositoryId;
import com.fl.adapter.in.rest.dto.response.MetricReportResponseDTO;
import com.fl.application.port.in.manage.ForManagingMetrics;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.time.Instant;
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
                var fromDate = Instant.parse(req.query().get("from"));
                var toDate = Instant.parse(req.query().get("to"));
                var hasName = req.query().contains("name");

                var response =
                    hasName
                        ? manage.getSpecificMetric(req.query().get("name"), fromDate, toDate)
                        : manage.getBetweenDates(fromDate, toDate);
                var body = response.stream().map(MetricReportResponseDTO::from).toList();
                res.status(200).send(body);
              } catch (NoSuchElementException e) {
                res.status(400).send(e);
              }
            });
  }
}
