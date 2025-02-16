package com.helidon.adapter.web;

import com.helidon.adapter.application.k6.domain.service.GetService;
import com.helidon.adapter.application.k6.domain.service.PostService;
import com.helidon.adapter.application.k6.in.GetHandler;
import com.helidon.adapter.application.k6.in.PostHandler;
import com.helidon.web.Webserver;
import io.helidon.config.Config;
import io.helidon.http.media.MediaContext;
import io.helidon.http.media.MediaContextConfig;
import io.helidon.http.media.MediaSupport;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelidonWebserver implements Webserver {

    public static Logger log = LoggerFactory.getLogger(HelidonWebserver.class);

    public static void setup() {
        GetService getService = new GetService();
        PostService postService = new PostService();

        GetHandler getHandler = new GetHandler(getService);
        PostHandler postHandler = new PostHandler(postService);

        Config config = Config.create();

        WebServer
                .builder().mediaContext(getMediaContext(config))
                .config(config.get("server"))
                .routing(HttpRouting.builder().register().get(getHandler::handleRequest).post(postHandler::handleRequest))
                .build()
                .start();

        log.info("HelidonWebserver started");
    }

    private static MediaContext getMediaContext(Config config) {
        MediaSupport jacksonSupport = JacksonSupport.create(config);

        return MediaContextConfig.builder().addMediaSupport(jacksonSupport).build();
    }
}

