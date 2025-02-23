package com.helidon.adapter.k6.out;

import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.Repository;

public class MetricJDBCRepository implements Repository {
  @Override
  public void save(Metrics metrics) {}

  @Override
  public Metrics get(String id) {
    return null;
  }
}
