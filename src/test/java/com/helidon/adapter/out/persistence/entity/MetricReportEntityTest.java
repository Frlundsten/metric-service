package com.helidon.adapter.out.persistence.entity;

import static com.helidon.MetricFactory.createMetricEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fl.adapter.out.persistence.entity.MetricEntity;
import com.fl.adapter.out.persistence.entity.MetricReportEntity;
import org.junit.jupiter.api.Test;

class MetricReportEntityTest {

  @Test
  void testAllArgsConstructor() {
    UUID id = UUID.randomUUID();
    String data =
        "{\"http_req_failed\":{\"type\":\"rate\",\"contains\":\"default\",\"values\":{\"rate\":0.0,\"passes\":0.0,\"fails\":100.0}},\"data_received\":{\"type\":\"counter\",\"contains\":\"data\",\"values\":{\"count\":10600.0,\"rate\":1058.5548080483122}},\"http_req_blocked\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":4.3165,\"min\":0.0,\"avg\":0.4172509999999999,\"med\":0.0,\"p(95)\":4.3165,\"p(90)\":0.49050000000003124}},\"http_req_receiving\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.6742,\"min\":0.0,\"avg\":0.027747,\"med\":0.0,\"p(95)\":0.053124999999998646,\"p(90)\":0.0}},\"iterations\":{\"type\":\"counter\",\"contains\":\"default\",\"values\":{\"count\":100.0,\"rate\":9.986366113663323}},\"data_sent\":{\"type\":\"counter\",\"contains\":\"data\",\"values\":{\"count\":8500.0,\"rate\":848.8411196613823}},\"iteration_duration\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1005.6491,\"min\":1000.3286,\"avg\":1001.3614669999998,\"med\":1000.9534,\"p(95)\":1005.08385,\"p(90)\":1001.57965}},\"http_req_sending\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.5284,\"min\":0.0,\"avg\":0.026425999999999998,\"med\":0.0,\"p(95)\":0.1374,\"p(90)\":0.0}},\"http_req_duration\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1.5789,\"min\":0.0,\"avg\":0.5493939999999998,\"med\":0.6349,\"p(95)\":1.0441,\"p(90)\":0.6742}},\"http_req_tls_handshaking\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.0,\"min\":0.0,\"avg\":0.0,\"med\":0.0,\"p(95)\":0.0,\"p(90)\":0.0}},\"vus_max\":{\"type\":\"gauge\",\"contains\":\"default\",\"values\":{\"value\":10.0,\"min\":10.0,\"max\":10.0}},\"http_req_waiting\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1.0505,\"min\":0.0,\"avg\":0.4952209999999996,\"med\":0.5302,\"p(95)\":0.691554999999999,\"p(90)\":0.6742}},\"http_req_connecting\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.5284,\"min\":0.0,\"avg\":0.02642,\"med\":0.0,\"p(95)\":0.026419999999998497,\"p(90)\":0.0}},\"vus\":{\"type\":\"gauge\",\"contains\":\"default\",\"values\":{\"value\":10.0,\"min\":10.0,\"max\":10.0}},\"http_reqs\":{\"type\":\"counter\",\"contains\":\"default\",\"values\":{\"count\":100.0,\"rate\":9.986366113663323}}}";
    Instant timestamp = Instant.now();
    List<MetricEntity> metricList = List.of(createMetricEntity());

    MetricReportEntity entity = new MetricReportEntity(id, data, timestamp, metricList);

    assertEquals(id, entity.id());
    assertEquals(data, entity.data());
    assertEquals(timestamp, entity.timestamp());
    assertEquals(metricList, entity.metricList());
  }

  @Test
  void testConstructorWithoutIdGeneratesId() {
    String data =
        "{\"http_req_failed\":{\"type\":\"rate\",\"contains\":\"default\",\"values\":{\"rate\":0.0,\"passes\":0.0,\"fails\":100.0}},\"data_received\":{\"type\":\"counter\",\"contains\":\"data\",\"values\":{\"count\":10600.0,\"rate\":1058.5548080483122}},\"http_req_blocked\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":4.3165,\"min\":0.0,\"avg\":0.4172509999999999,\"med\":0.0,\"p(95)\":4.3165,\"p(90)\":0.49050000000003124}},\"http_req_receiving\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.6742,\"min\":0.0,\"avg\":0.027747,\"med\":0.0,\"p(95)\":0.053124999999998646,\"p(90)\":0.0}},\"iterations\":{\"type\":\"counter\",\"contains\":\"default\",\"values\":{\"count\":100.0,\"rate\":9.986366113663323}},\"data_sent\":{\"type\":\"counter\",\"contains\":\"data\",\"values\":{\"count\":8500.0,\"rate\":848.8411196613823}},\"iteration_duration\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1005.6491,\"min\":1000.3286,\"avg\":1001.3614669999998,\"med\":1000.9534,\"p(95)\":1005.08385,\"p(90)\":1001.57965}},\"http_req_sending\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.5284,\"min\":0.0,\"avg\":0.026425999999999998,\"med\":0.0,\"p(95)\":0.1374,\"p(90)\":0.0}},\"http_req_duration\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1.5789,\"min\":0.0,\"avg\":0.5493939999999998,\"med\":0.6349,\"p(95)\":1.0441,\"p(90)\":0.6742}},\"http_req_tls_handshaking\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.0,\"min\":0.0,\"avg\":0.0,\"med\":0.0,\"p(95)\":0.0,\"p(90)\":0.0}},\"vus_max\":{\"type\":\"gauge\",\"contains\":\"default\",\"values\":{\"value\":10.0,\"min\":10.0,\"max\":10.0}},\"http_req_waiting\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1.0505,\"min\":0.0,\"avg\":0.4952209999999996,\"med\":0.5302,\"p(95)\":0.691554999999999,\"p(90)\":0.6742}},\"http_req_connecting\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.5284,\"min\":0.0,\"avg\":0.02642,\"med\":0.0,\"p(95)\":0.026419999999998497,\"p(90)\":0.0}},\"vus\":{\"type\":\"gauge\",\"contains\":\"default\",\"values\":{\"value\":10.0,\"min\":10.0,\"max\":10.0}},\"http_reqs\":{\"type\":\"counter\",\"contains\":\"default\",\"values\":{\"count\":100.0,\"rate\":9.986366113663323}}}";
    Instant timestamp = Instant.now();
    List<MetricEntity> metricList = List.of(createMetricEntity());

    MetricReportEntity entity = new MetricReportEntity(data, timestamp, metricList);

    assertNotNull(entity.id());
    assertEquals(data, entity.data());
    assertEquals(timestamp, entity.timestamp());
    assertEquals(metricList, entity.metricList());
  }
}
