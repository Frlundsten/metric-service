package com.helidon.application.port.in.create;

import com.helidon.application.domain.model.K6Metrics;

public interface ForCreateMetrics {
    void saveMetrics(K6Metrics k6Metrics);
}
