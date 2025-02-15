package com.helidon.adapter.application.k6.domain;

import com.helidon.application.domain.model.Type;

public enum K6Type implements Type {
    TIME,
    GAUGE,
    COUNTER,
    RATE
}
