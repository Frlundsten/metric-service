package com.helidon.application.domain;

import com.helidon.application.domain.model.K6Type;
import com.helidon.application.domain.model.Metric;
import com.helidon.application.domain.model.Values;

public record GaugeMetric(String id, String name, K6Type type, GaugeValues values)
    implements Metric {
  public record GaugeValues(double value, double min, double max) implements Values {}
}
