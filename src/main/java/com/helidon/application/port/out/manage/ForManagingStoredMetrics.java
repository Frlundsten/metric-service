package com.helidon.application.port.out.manage;

import com.helidon.application.domain.model.Metrics;

import java.time.Instant;
import java.util.List;

public interface ForManagingStoredMetrics {
  /**
   * Get full metrics from an id.
   *
   * @param id
   * @return Metrics object with provided id
   */
  Metrics get(String id);

  /**
   * Get metrics objects from a timespan.
   *
   * @param start Inclusive
   * @param end Exclusive
   * @return List of Metrics objects
   */
  List<Metrics> getBetweenDates(Instant start, Instant end);
}
