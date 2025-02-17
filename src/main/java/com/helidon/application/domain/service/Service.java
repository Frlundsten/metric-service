package com.helidon.application.domain.service;

import com.helidon.application.domain.model.Metrics;

public interface Service {
  void saveMetrics(Metrics metrics);
}
