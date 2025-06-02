package com.fl.adapter.in.rest;

import com.fl.adapter.in.rest.dto.request.AiMetricReportRequest;
import com.fl.application.port.out.ai.ForContactingAI;
import io.helidon.common.GenericType;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

import java.util.List;

public class AiHandler implements Handler {
    ForContactingAI forContactingAI;

    public AiHandler(ForContactingAI forContactingAI) {
        this.forContactingAI = forContactingAI;
    }

    @Override
    public void handle(ServerRequest req, ServerResponse res) {
        var body = req.content().as(new GenericType<List<AiMetricReportRequest>>(){});
        System.out.println(body);
        if (body.isEmpty()) {
            forContactingAI.analyzeWithAi();
        }
        forContactingAI.analyzeWithAi(body);
    }
}
