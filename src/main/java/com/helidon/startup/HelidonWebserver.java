package com.helidon.startup;

import com.helidon.adapter.k6.in.GetHandler;
import com.helidon.adapter.k6.in.Mapper;
import com.helidon.adapter.k6.in.PostHandler;
import com.helidon.application.service.GetService;
import com.helidon.application.service.PostService;
import io.helidon.config.Config;
import io.helidon.http.media.MediaContext;
import io.helidon.http.media.MediaContextConfig;
import io.helidon.http.media.MediaSupport;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelidonWebserver {

  public static Logger LOG = LoggerFactory.getLogger(HelidonWebserver.class);

  public static void setup() {
    Mapper mapper = new Mapper();
    GetService getService = new GetService();
    PostService postService = new PostService();

    GetHandler getHandler = new GetHandler(getService);
    PostHandler postHandler = new PostHandler(postService, mapper);

    Config config = Config.create();

    WebServer.builder()
        .mediaContext(getMediaContext(config))
        .config(config.get("server"))
        .routing(
            HttpRouting.builder()
                .register()
                .get(getHandler::handleRequest)
                .post(postHandler::handleRequest))
        .build()
        .start();

    LOG.info("HelidonWebserver started");
  }

  private static MediaContext getMediaContext(Config config) {
    MediaSupport jacksonSupport = JacksonSupport.create(config);

    return MediaContextConfig.builder().addMediaSupport(jacksonSupport).build();
  }
}
