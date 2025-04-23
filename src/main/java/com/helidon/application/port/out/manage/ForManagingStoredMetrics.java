package com.helidon.application.port.out.manage;

import com.helidon.application.domain.model.MetricReport;

import java.time.Instant;
import java.util.List;

public interface ForManagingStoredMetrics {
  /**
   * Get full metric report from an id.
   *
   * @param id Id of the wanted metrics-report
   * @return MetricReport object with provided id
   */
  MetricReport get(String id);

  /**
   * Get metrics objects from a timespan.
   *
   * @param start Inclusive
   * @param end Exclusive
   * @return List of MetricReport objects
   */
  List<MetricReport> getBetweenDates(Instant start, Instant end);
}
