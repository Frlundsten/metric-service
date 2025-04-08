package com.helidon.application.domain;

import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Values;

public record CounterMetric(String id, String name, K6Type type, CounterValues values)
    implements Metric {
  public record CounterValues(double count, double rate) implements Values {}
}
