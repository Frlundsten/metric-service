package com.helidon.application.port.in.manage;

import com.helidon.application.domain.model.Metrics;

public interface ForManagingMetrics {
  Metrics getMetrics(String id);
}
