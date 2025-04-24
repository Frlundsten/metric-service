package com.helidon.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.helidon.adapter.Mapper;
import com.helidon.adapter.in.rest.dto.TrendValuesDTO;
import com.helidon.adapter.in.rest.dto.request.MetricRequestDTO;
import com.helidon.application.domain.model.MetricReport;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapperTest {
  Mapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new Mapper();
  }

  @Test
  void mapToDomain() {

    Map<String, MetricRequestDTO> map =
        Map.of(
            "http_req_receiving",
            new MetricRequestDTO(
                "trend",
                "time",
                new TrendValuesDTO(0.6742, 0.0, 0.027747, 0.0, 0.053124999999998646, 0.0)));
    var result = mapper.toDomain(map);
    assertThat(result).isNotNull().isInstanceOf(MetricReport.class);
    assertThat(result.metricList()).hasSize(1);
  }
}
