package com.helidon.application.domain.service;

import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.in.create.ForCreateMetrics;
import com.helidon.application.port.in.manage.ForManagingMetrics;
import com.helidon.application.port.out.create.ForPersistingMetrics;
import com.helidon.application.port.out.manage.ForManagingStoredMetrics;

public class MetricService implements ForCreateMetrics, ForManagingMetrics {

  private final ForPersistingMetrics persistingMetrics;
  private final ForManagingStoredMetrics manageStoredMetrics;

  public MetricService(
      ForPersistingMetrics persistingMetrics, ForManagingStoredMetrics manageStoredMetrics) {
    this.persistingMetrics = persistingMetrics;
    this.manageStoredMetrics = manageStoredMetrics;
  }

  @Override
  public void saveMetrics(Metrics metrics) {
    persistingMetrics.saveMetrics(metrics);
  }

  @Override
  public Metrics getMetrics(String id) {
    return manageStoredMetrics.get(id);
  }
}
