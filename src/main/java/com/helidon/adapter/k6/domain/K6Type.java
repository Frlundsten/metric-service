package com.helidon.adapter.k6.domain;

import com.helidon.application.domain.model.Type;

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
