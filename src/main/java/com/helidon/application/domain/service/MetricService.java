package com.helidon.application.domain.service;

import com.helidon.application.domain.model.MetricReport;
import com.helidon.application.port.in.create.ForCreateMetrics;
import com.helidon.application.port.in.manage.ForManagingMetrics;
import com.helidon.application.port.out.create.ForAlertingUser;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;

public class MetricService implements ForCreateMetrics, ForManagingMetrics {

  private final ForPersistingMetrics persistingMetrics;
  private final ForManagingStoredMetrics manageStoredMetrics;
  private final AlarmService alarmService;

  public MetricService(
      ForPersistingMetrics persistingMetrics, ForManagingStoredMetrics manageStoredMetrics, ForAlertingUser alertingUser) {
    this.persistingMetrics = persistingMetrics;
    this.manageStoredMetrics = manageStoredMetrics;
    this.alarmService = new AlarmService(alertingUser);
  }

  @Override
  public void saveMetrics(MetricReport metricReport) {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      scope.fork(
          () -> {
            persistingMetrics.saveMetrics(metricReport);
            return null;
          });
      scope.fork(
          () -> {
            checkAlarms(metricReport);
            return null;
          });
    }
  }

  private void checkAlarms(MetricReport metricReport) {
    var reqDuration =
        metricReport.metricList().stream()
            .filter(metric -> metric.name().value().equals("http_req_duration"))
            .findFirst();

    if (reqDuration.isEmpty()) {
      return;
    }
    var recent = manageStoredMetrics.getMetricFromRecentRuns(reqDuration.get(), 5);

    alarmService.check(recent, reqDuration.get());
  }

  @Override
  public MetricReport getMetrics(String id) {
    return manageStoredMetrics.get(id);
  }

  @Override
  public List<MetricReport> getBetweenDates(Instant from, Instant to) {
    return manageStoredMetrics.getBetweenDates(from, to);
  }

  @Override
  public List<MetricReport> getRecent() {
    return manageStoredMetrics.getRecentFromView();
  }

  @Override
  public List<MetricReport> getSpecificMetric(String name, Instant start, Instant end) {
    return manageStoredMetrics.getBetweenDates(name, start, end);
  }
}
