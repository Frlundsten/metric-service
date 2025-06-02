package com.fl.application.port.out.ai;

import com.fl.adapter.in.rest.dto.request.AiMetricReportRequest;
import java.util.List;

public interface ForContactingAI {
    void analyzeWithAi();
    void analyzeWithAi(List<AiMetricReportRequest> reports);
}
