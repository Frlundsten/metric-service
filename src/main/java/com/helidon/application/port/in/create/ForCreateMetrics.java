package com.helidon.application.port.in.create;

import com.helidon.application.domain.model.Metrics;

public interface ForCreateMetrics {
    void saveMetrics(Metrics metrics);
}
