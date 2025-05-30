package com.fl.adapter.in.rest;

import com.fl.adapter.common.RepositoryId;
import com.fl.adapter.in.rest.dto.response.MetricReportResponseDTO;
import com.fl.application.port.in.manage.ForManagingMetrics;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecentReportsHandler implements Handler {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final ForManagingMetrics manage;

  public RecentReportsHandler(ForManagingMetrics manage) {
    this.manage = manage;
  }

  @Override
  public void handle(ServerRequest req, ServerResponse res) {
    LOG.debug("Processing a GET request for recent reports..");
    var repoIdValue = req.headers().get(HeaderNames.create("Repository-Id"));
    RepositoryId repositoryId = new RepositoryId(repoIdValue.values());
    ScopedValue.where(RepositoryId.REPOSITORY_ID, repositoryId)
        .run(
            () -> {
              try {
                var response = handleRequest();
                res.status(200).send(response);
              } catch (NoSuchElementException e) {
                res.status(400).send(e);
              }
            });
  }

  private List<MetricReportResponseDTO> handleRequest() {
    var metricsList = manage.getRecent();
    return metricsList.stream().map(MetricReportResponseDTO::from).toList();
  }
}
