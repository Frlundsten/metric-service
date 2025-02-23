package com.helidon.adapter.k6.in;

import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.in.Handler;
import com.helidon.application.service.PostService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostHandler implements Handler {
  public static Logger LOG = LoggerFactory.getLogger(PostHandler.class);
  private final PostService postService;
  private final Mapper mapper;

  public PostHandler(PostService postService, Mapper mapper) {
    this.postService = postService;
    this.mapper = mapper;
  }

  public void handleRequest(ServerRequest req, ServerResponse res) {
    LOG.info("Processing a post request..");
    var reqBody = req.content().as(MetricsRequestDTO.class);

    Metrics metrics = mapper.fromDTO(reqBody.metrics());

    postService.saveMetrics(metrics);
    res.status(200);
    res.send(metrics);
  }
}
