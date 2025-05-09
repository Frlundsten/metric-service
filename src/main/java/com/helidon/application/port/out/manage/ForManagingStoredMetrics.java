package com.helidon.application.port.out.manage;

import com.helidon.application.domain.model.MetricReport;

import java.time.Instant;
import java.util.List;

public interface ForManagingStoredMetrics {
  /**
   * Get metric report from id.
   *
   * @param id Id of the wanted metric report
   * @return Metric report object from provided id
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

  /**
   * Retrieves metric reports from a fixed view.
   * This operation returns reports from a recent timespan, typically based on a predefined time range.
   *
   * @return a list of {@link MetricReport} objects containing the metric reports.
   */
  List<MetricReport> getRecentFromView();

  /**
   * Get metric value from a specific timespan
   *
   * @param name Name of the metric
   * @param start Inclusive
   * @param end Exclusive
   * @return List of metrics
   */
  List<MetricReport> getBetweenDates(String name, Instant start, Instant end);
}
