package com.helidon.application.domain.service;

import com.fl.application.domain.model.K6Type;
import com.fl.application.domain.model.Metric;
import com.fl.application.domain.model.MetricName;
import com.fl.application.domain.model.MetricReport;
import com.fl.application.domain.model.TrendValues;
import com.fl.application.domain.service.AlarmService;
import com.fl.application.port.out.notification.ForAlertingUser;
import io.helidon.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AlarmServiceTest {
    ForAlertingUser forAlertingUser;
    AlarmService alarmService;
    Config config = Config.create();

    @BeforeEach
    void setUp() {
        forAlertingUser = mock(ForAlertingUser.class);
        alarmService = new AlarmService(forAlertingUser);
    }

    @Test
    void shouldAlertUserWhenIncreasingOverTime() {
        var reports = new ArrayList<MetricReport>();
        var RECENT_RUN_SPAN = config.get("alarm.http.duration.span").asInt().get();
        var P95_THRESHOLD_ITERATION = 1 + config.get("alarm.http.duration.increase.threshold").asDouble().get() / 100;
        var FIRST_TO_LAST_THRESHOLD = 1 + config.get("alarm.http.duration.increase.first-to-last").asDouble().get() / 100;

        for (int i = 0; i < RECENT_RUN_SPAN; i++) {
            double p95 = 100.01 - (i * 10);
            reports.add(
                    new MetricReport(
                            UUID.randomUUID(),
                            "{}",
                            Instant.now(),
                            List.of(
                                    new Metric(
                                            new MetricName("http_req_duration"),
                                            K6Type.TREND,
                                            new TrendValues(44.00, 0.50, 1.37, 1.24, p95, 110.10)))));
        }

        assertThatNoException().isThrownBy(() -> alarmService.check(reports));

        verify(forAlertingUser, times(1))
                .sendAlert("""
                                P95 has consistently increased over the selected period!
                                Settings used:
                                Iteration increase threshold: %.0f%%
                                First to last threshold: %.0f%% ❌
                                Recent runs span: %d
                                """.formatted(
                                (P95_THRESHOLD_ITERATION - 1) * 100,
                                (FIRST_TO_LAST_THRESHOLD - 1) * 100,
                                RECENT_RUN_SPAN),
                        reports);
    }
}
