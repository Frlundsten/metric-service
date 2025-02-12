package com.helidon.adapter.web;

import com.helidon.adapter.application.in.GetHandler;
import com.helidon.adapter.application.in.PostHandler;
import com.helidon.adapter.application.domain.service.GetService;
import com.helidon.adapter.application.domain.service.PostService;
import com.helidon.web.Webserver;
import io.helidon.config.Config;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;

public class HelidonWebserver implements Webserver {
    public static void setup() {
        GetService getService = new GetService();
        PostService postService = new PostService();

        GetHandler getHandler = new GetHandler(getService);
        PostHandler postHandler = new PostHandler(postService);

        Config config = Config.create();

        WebServer
                .builder()
                .config(config.get("server"))
                .routing(HttpRouting.builder().get(getHandler::handleRequest).post(postHandler::handleRequest))
                .build()
                .start();
    }
}

