package com.helidon.startup;

import io.helidon.config.Config;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.HttpService;

public class HelidonWebserver {

  public static void setup(HttpService service) {

    Config config = Config.create();

    WebServer.builder()
        .config(config.get("server"))
        .routing(HttpRouting.builder().register("/metrics", service))
        .build()
        .start();
  }
}
