package com.helidon.application.domain.model;

public enum K6Type implements Type {
  TREND,
  GAUGE,
  COUNTER,
  RATE;

  @Override
  public String getType() {
    return this.name();
  }
}
