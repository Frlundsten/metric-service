package com.helidon.application.port.in;

import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public interface Handler {
    void handleRequest(ServerRequest req, ServerResponse res);
}
