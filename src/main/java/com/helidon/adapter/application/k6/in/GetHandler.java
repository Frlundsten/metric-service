package com.helidon.adapter.application.k6.in;

import com.helidon.adapter.application.k6.domain.service.GetService;
import com.helidon.application.port.in.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class GetHandler implements Handler {
    private final GetService getService;

    public GetHandler(GetService getService) {
        this.getService = getService;
    }

    public void handleRequest(ServerRequest req, ServerResponse res) {
        getService.sayHello();
        res.status(202);
        res.send();
    }
}
