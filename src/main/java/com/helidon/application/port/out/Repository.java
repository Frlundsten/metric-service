package com.helidon.application.port.out;

import com.helidon.application.domain.model.Metrics;

public interface Repository {
    void save(Metrics metrics);
    Metrics get(String id);
}
