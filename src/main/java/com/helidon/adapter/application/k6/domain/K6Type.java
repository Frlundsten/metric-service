package com.helidon.adapter.application.k6.domain;

import com.helidon.application.domain.model.Type;

public enum K6Type implements Type {
  TREND,
  GAUGE,
  COUNTER,
  RATE;
}
