package com.helidon.application.port.in.manage;

import com.helidon.application.domain.model.MetricReport;

import java.time.Instant;
import java.util.List;

public interface ForManagingMetrics {

  /**
   * Get full metrics from an id.
   *
   * @param id
   * @return MetricReport object with provided id
   */
  MetricReport getMetrics(String id);

  /**
   * Get metrics objects from a timespan.
   *
   * @param start Inclusive
   * @param end Exclusive
   * @return List of MetricReport objects
   */
  List<MetricReport> getBetweenDates(Instant start, Instant end);
}
