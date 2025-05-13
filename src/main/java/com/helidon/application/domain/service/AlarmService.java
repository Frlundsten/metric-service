package com.helidon.application.domain.service;

import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.MetricReport;
import com.helidon.application.domain.model.TrendValues;
import com.helidon.application.port.out.create.ForAlertingUser;
import com.helidon.exception.TrendViolationException;
import io.helidon.config.Config;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmService {

  private final ForAlertingUser alertUser;

  Config config = Config.create();
  private final int REQ_DURATION_SPAN = config.get("alarm.http.duration.span").asInt().get();
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public AlarmService(ForAlertingUser alertUser) {
    this.alertUser = alertUser;
  }

  public void check(List<MetricReport> recent, Metric current) {
    LOG.debug("Performing alarm check..");
    var trendList =
        recent.stream()
            .flatMap(
                report -> report.metricList().stream().map(metric -> (TrendValues) metric.values()))
            .collect(Collectors.toCollection(ArrayList::new));
    trendList.add((TrendValues) current.values());

    try {
      checkTrend(trendList);
      LOG.debug("Alarm check passed");
    } catch (TrendViolationException e) {
      LOG.warn("Alarm check failed", e);
      alertUser.sendAlert(e.getMessage(),recent);
    }
  }

  private void checkTrend(List<TrendValues> trendList) {
    if (trendList.size() < 2) {
      return;
    }
    for (int i = 0; i < REQ_DURATION_SPAN; i++) {
      if (trendList.get(i).p95() > trendList.get(i + 1).p95()) {
        return;
      }
    }
    throw new TrendViolationException(
        "The trend has consistently increased over the past " + REQ_DURATION_SPAN + " periods");
  }
}
