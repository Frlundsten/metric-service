package com.helidon.application.port.out.create;

import com.helidon.application.domain.model.K6Metrics;

public interface ForPersistingMetrics {
    void saveMetrics(K6Metrics k6Metrics);
}
