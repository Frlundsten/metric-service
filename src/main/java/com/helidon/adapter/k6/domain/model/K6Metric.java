package com.helidon.adapter.k6.domain.model;

import com.helidon.adapter.k6.domain.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Values;

public record K6Metric(String name, K6Type type, Values values) implements Metric {}
