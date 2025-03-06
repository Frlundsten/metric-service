package com.helidon.application.port.out.manage;

import com.helidon.application.domain.model.Metrics;

public interface ForManagingStoredMetrics {
  Metrics get(String id);
}
