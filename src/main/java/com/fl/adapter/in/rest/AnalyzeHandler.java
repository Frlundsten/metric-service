package com.fl.adapter.in.rest;

import com.fl.adapter.in.rest.dto.request.AiMetricReportRequest;
import com.fl.application.domain.model.K6Type;
import com.fl.application.port.in.analyze.ForAnalyzingData;
import com.fl.exception.AnalyzeAdapterException;
import io.helidon.common.GenericType;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AnalyzeHandler implements Handler {
    Logger LOG = LoggerFactory.getLogger(AnalyzeHandler.class);
    private final ForAnalyzingData forAnalyzingData;

    public AnalyzeHandler(ForAnalyzingData forAnalyzingData) {
        this.forAnalyzingData = forAnalyzingData;
    }

    @Override
    public void handle(ServerRequest req, ServerResponse res) {
        LOG.debug("Request to analyze data...");
        var reportDTOList = req.content().as(new GenericType<List<AiMetricReportRequest>>() {
        });

        reportDTOList = keepOnlyTrend(reportDTOList);

        if (reportDTOList.isEmpty()) {
            LOG.debug("Analyzing Recent runs");
            forAnalyzingData.analyzeRecentRuns();
        }

        LOG.debug("Analyzing {} reports", reportDTOList.size());
        var domainList = reportDTOList.stream()
                .map(AiMetricReportRequest::toDomain)
                .toList();
        try {
            var response = forAnalyzingData.analyzeData(domainList);
            res.status(200).send(response);
        } catch (AnalyzeAdapterException e) {
            res.status(503).send(e.getMessage());
        }
    }

    private List<AiMetricReportRequest> keepOnlyTrend(List<AiMetricReportRequest> reportDTOList) {
        return reportDTOList.stream()
                .map(report -> new AiMetricReportRequest(
                        report.id(),
                        report.timestamp(),
                        report.metrics().stream()
                                .filter(metric -> K6Type.TREND.name().equalsIgnoreCase(metric.type()))
                                .toList()
                ))
                .filter(report -> !report.metrics().isEmpty())
                .toList();
    }
}
