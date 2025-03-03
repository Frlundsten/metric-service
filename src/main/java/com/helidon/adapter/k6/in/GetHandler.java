package com.helidon.adapter.k6.in;

import com.helidon.application.service.GetService;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class GetHandler implements Handler {
  private final GetService getService;

  public GetHandler(GetService getService) {
    this.getService = getService;
  }

  @Override
  public void handle(ServerRequest req, ServerResponse res) {
    //        getService.sayHello();
    res.status(202);
    res.send();
  }
}
