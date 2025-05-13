package com.helidon.application.domain.service;

import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.Mockito.mock;

import com.helidon.application.domain.model.*;
import com.helidon.application.port.out.create.ForAlertingUser;
import com.helidon.exception.TrendViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.helidon.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
  void shouldThrowExceptionWhenIncreasingOverTime() {
    var reports = new ArrayList<MetricReport>();
    var currentMetric =
        new Metric(
            new MetricName("http_req_duration"),
            K6Type.TREND,
            new TrendValues(44.00, 0.50, 1.37, 1.24, 160, 110.10));
    var latestRuns = config.get("alarm.http.duration.span").asInt().get();

    for (int i = 0; i < latestRuns; i++) {
      double p95 = 100.01 + (i * 10);
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

    assertThatException()
        .isThrownBy(() -> alarmService.check(reports, currentMetric))
        .isInstanceOf(TrendViolationException.class)
        .withMessage("The trend has consistently increased over the past 5 periods");
  }
}
