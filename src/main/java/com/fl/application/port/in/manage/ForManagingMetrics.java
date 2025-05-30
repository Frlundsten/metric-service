package com.fl.application.port.in.manage;

import com.fl.application.domain.model.MetricReport;
import java.time.Instant;
import java.util.List;

public interface ForManagingMetrics {

  /**
   * Get a single metric report from an id.
   *
   * @param id
   * @return MetricReport object with provided id
   */
  MetricReport getMetrics(String id);

  /**
   * Get metric reports from a timespan.
   *
   * @param start Inclusive
   * @param end Exclusive
   * @return List of MetricReport objects
   */
  List<MetricReport> getBetweenDates(Instant start, Instant end);

  /**
   * Get recent metric reports.
   *
   * @return List of metric reports.
   */
  List<MetricReport> getRecent();

  /**
   * Get metric value from a specific timespan
   *
   * @param name Name of field
   * @param start Inclusive
   * @param end Exclusive
   * @return List of metric values
   */
  List<MetricReport> getSpecificMetric(String name, Instant start, Instant end);
}
