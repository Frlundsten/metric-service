package com.helidon.application.domain.model;

public enum K6Type {
  RATE(RateValues.class),
  TREND(TrendValues.class),
  GAUGE(GaugeValues.class),
  COUNTER(CounterValues.class);

  private final Class<? extends Values> valueClass;

  K6Type(Class<? extends Values> valueClass) {
    this.valueClass = valueClass;
  }

  public Class<? extends Values> getValueClass() {
    return valueClass;
  }
}
