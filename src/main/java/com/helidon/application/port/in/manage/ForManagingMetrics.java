package com.helidon.application.port.in.manage;

import com.helidon.application.domain.model.K6Metrics;
import java.time.Instant;
import java.util.List;

public interface ForManagingMetrics {

  /**
   * Get full metrics from an id.
   *
   * @param id
   * @return Metrics object with provided id
   */
  K6Metrics getMetrics(String id);

  /**
   * Get metrics objects from a timespan.
   *
   * @param start Inclusive
   * @param end Exclusive
   * @return List of Metrics objects
   */
  List<K6Metrics> getBetweenDates(Instant start, Instant end);
}
