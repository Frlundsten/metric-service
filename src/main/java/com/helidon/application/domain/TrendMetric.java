package com.helidon.application.domain;

import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Values;

public record TrendMetric(String id, String name, K6Type type, TrendValues values)
    implements Metric {
  public record TrendValues(double max, double min, double avg, double med, double p95, double p90)
      implements Values {}
}
