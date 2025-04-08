package com.helidon.application.domain;

import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Values;

public record RateMetric(String id, String name, K6Type type, RateValues values) implements Metric {
  public record RateValues(double rate, double passes, double fails) implements Values {}
}
