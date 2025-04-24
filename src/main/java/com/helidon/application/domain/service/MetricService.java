package com.helidon.application.domain.service;

import com.helidon.application.domain.model.MetricReport;
import com.helidon.application.port.in.create.ForCreateMetrics;
import com.helidon.application.port.in.manage.ForManagingMetrics;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;
import java.time.Instant;
import java.util.List;

public class MetricService implements ForCreateMetrics, ForManagingMetrics {

  private final ForPersistingMetrics persistingMetrics;
  private final ForManagingStoredMetrics manageStoredMetrics;

  public MetricService(
      ForPersistingMetrics persistingMetrics, ForManagingStoredMetrics manageStoredMetrics) {
    this.persistingMetrics = persistingMetrics;
    this.manageStoredMetrics = manageStoredMetrics;
  }

  @Override
  public void saveMetrics(MetricReport metricReport) {
    persistingMetrics.saveMetrics(metricReport);
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
}
