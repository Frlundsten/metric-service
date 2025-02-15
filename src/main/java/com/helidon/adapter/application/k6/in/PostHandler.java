package com.helidon.adapter.application.k6.in;

import com.helidon.adapter.application.k6.domain.service.PostService;
import com.helidon.application.port.in.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class PostHandler implements Handler {
    private final PostService postService;

    public PostHandler(PostService postService) {
        this.postService = postService;
    }

    public void handleRequest(ServerRequest req, ServerResponse res) {
        postService.sayHello();
        res.status(422);
        res.send();
    }
}
