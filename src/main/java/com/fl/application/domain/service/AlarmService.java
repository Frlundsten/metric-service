package com.fl.application.domain.service;

import com.fl.application.domain.model.MetricReport;
import com.fl.application.domain.model.TrendValues;
import com.fl.application.port.out.notification.ForAlertingUser;
import com.fl.exception.TrendViolationException;
import io.helidon.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlarmService {

    private final ForAlertingUser alertUser;

    Config config = Config.create();
    private final int REQ_DURATION_SPAN = config.get("alarm.http.duration.span").asInt().get();
    private final double P95_THRESHOLD_ITERATION = 1 + config.get("alarm.http.duration.increase.threshold").asDouble().get() / 100;
    private final double FIRST_TO_LAST_THRESHOLD = 1 + config.get("alarm.http.duration.increase.first-to-last").asDouble().get() / 100;
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public AlarmService(ForAlertingUser alertUser) {
        this.alertUser = alertUser;
    }

    public void check(List<MetricReport> recent) {
        LOG.debug("Prepare check of p95 values");
        var p95List =
                recent.stream()
                        .flatMap(
                                report -> report.metricList().stream().map(metric -> (TrendValues) metric.values()).map(TrendValues::p95))
                        .collect(Collectors.toCollection(ArrayList::new)).reversed();

        try {
            checkP95Trend(p95List);
            LOG.debug("Alarm check passed");
        } catch (TrendViolationException e) {
            LOG.warn("Alarm check failed", e);
            alertUser.sendAlert(e.getMessage(), recent);
        }
    }

    /**
     * Checks if the p95 values show a consistent upward trend.
     * The method verifies two conditions:
     * <ul>
     *   <li>Each consecutive p95 value must be greater than the previous by at least a defined threshold (e.g. 5%).</li>
     *   <li>The overall increase from the first to the last value will be validated.</li>
     * </ul>
     * If first condition is met over the specified number of periods, a {@link TrendViolationException} is thrown.
     *
     * @param p95List List of p95 latency values, ordered from oldest to newest.
     * @throws TrendViolationException if a consistent and significant increase is detected.
     */
    protected void checkP95Trend(List<Double> p95List) {
        LOG.debug("Performing alarm check with {} p95 values", p95List.size());
        if (p95List.size() < REQ_DURATION_SPAN) {
            LOG.debug("Wanted to perform a check on {} reports but could only find {}", REQ_DURATION_SPAN,p95List.size());
            return;
        }

        double first = p95List.getFirst();
        double last = p95List.getLast();

        var firstToLastCheck = last < first * FIRST_TO_LAST_THRESHOLD ? "✅" : "❌";

        for (int i = 0; i < REQ_DURATION_SPAN - 1; i++) {
            if (p95List.get(i) * P95_THRESHOLD_ITERATION >= p95List.get(i + 1)) {
                return;
            }
        }

        throw new TrendViolationException(
                """
                        P95 has consistently increased over the selected period!
                        Settings used:
                        Iteration increase threshold: %.0f%%
                        First to last threshold: %.0f%% %s
                        Recent runs span: %d
                        """.formatted(
                        (P95_THRESHOLD_ITERATION - 1) * 100,
                        (FIRST_TO_LAST_THRESHOLD - 1) * 100,
                        firstToLastCheck,
                        REQ_DURATION_SPAN));
    }
}
