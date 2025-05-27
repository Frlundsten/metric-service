package com.fl.application.domain.service;

import com.fl.application.domain.model.MetricReport;
import com.fl.application.port.in.create.ForCreateMetrics;
import com.fl.application.port.in.manage.ForManagingMetrics;
import com.fl.application.port.out.create.ForAlertingUser;
import com.fl.application.port.out.create.ForPersistingMetrics;
import com.fl.application.port.out.manage.ForManagingStoredMetrics;
import java.time.Instant;
import java.util.List;

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
            persistingMetrics.saveMetrics(metricReport);
            checkAlarms(metricReport);
  }

  private void checkAlarms(MetricReport metricReport) {
    var reqDuration =
        metricReport.metricList().stream()
            .filter(metric -> metric.name().value().equals("http_req_duration"))
            .findFirst();

    if (reqDuration.isEmpty()) {
      return;
    }

    var recentReports = manageStoredMetrics.getMetricFromRecentRuns(reqDuration.get(), 5);

    alarmService.check(recentReports, reqDuration.get());
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
