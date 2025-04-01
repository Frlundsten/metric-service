package com.helidon.application.domain.service;

import com.helidon.application.domain.model.K6Metrics;
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
  public void saveMetrics(K6Metrics k6Metrics) {
    persistingMetrics.saveMetrics(k6Metrics);
  }

  @Override
  public K6Metrics getMetrics(String id) {
    return manageStoredMetrics.get(id);
  }

  @Override
  public List<K6Metrics> getBetweenDates(Instant from, Instant to) {
    return manageStoredMetrics.getBetweenDates(from, to);
  }
}
