package com.helidon.application.port.out.create;

import com.helidon.application.domain.model.Metrics;

public interface ForPersistingMetrics {
    void saveMetrics(Metrics metrics);
}
