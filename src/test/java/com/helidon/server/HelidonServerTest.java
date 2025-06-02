package com.helidon.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fl.adapter.in.rest.AiHandler;
import com.fl.adapter.in.rest.CreateMetricsHandler;
import com.fl.adapter.in.rest.DelegatingService;
import com.fl.adapter.in.rest.RecentReportsHandler;
import com.fl.adapter.in.rest.ReportTimespanHandler;
import com.fl.application.port.in.create.ForCreateMetrics;
import com.fl.application.port.in.manage.ForManagingMetrics;
import com.fl.adapter.common.Mapper;
import com.fl.adapter.common.ObjectMapperFactory;
import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
import io.helidon.http.Status;
import io.helidon.webclient.http1.Http1Client;
import io.helidon.webclient.http1.Http1ClientResponse;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.testing.junit5.ServerTest;
import io.helidon.webserver.testing.junit5.SetUpRoute;
import java.util.Map;
import org.junit.jupiter.api.Test;

@ServerTest
class HelidonServerTest {
  final Http1Client client;
  final HeaderName repoHeader = HeaderNames.create("Repository-Id");
  final String repoId = "test-repo";
  final ObjectMapper mapper = ObjectMapperFactory.create();

  public HelidonServerTest(Http1Client client) {
    this.client = client;
  }

  @SetUpRoute
  static void routing(HttpRouting.Builder builder) {
    ReportTimespanHandler reportTimespanHandler = new ReportTimespanHandler(mock(ForManagingMetrics.class));
    AiHandler aiHandler = mock(AiHandler.class);
    CreateMetricsHandler createMetricsHandler =
        new CreateMetricsHandler(mock(ForCreateMetrics.class), mock(Mapper.class));
    RecentReportsHandler recentReportsHandler = new RecentReportsHandler(mock(ForManagingMetrics.class));
    var fooService = new DelegatingService(createMetricsHandler, reportTimespanHandler,recentReportsHandler, aiHandler);
    fooService.routing(builder);
  }

  @Test
  void shouldReturn200WhenValidGetBetweenDate() {
    try (Http1ClientResponse response =
        client
            .get("/metrics")
            .queryParam("from", "2025-04-22T00:00:00Z")
            .queryParam("to", "2025-04-24T00:00:00Z")
            .header(repoHeader, repoId)
            .request()) {
      assertThat(response.status()).isEqualTo(Status.OK_200);
    }
  }

  @Test
  void shouldReturn201WhenValidPost() throws JsonProcessingException {
    Map<String, Object> jsonBody = mapper.readValue(reportRequestBody, new TypeReference<>() {});

    try (Http1ClientResponse response =
        client
            .post("/metrics")
            .header(repoHeader, repoId)
            .header(HeaderNames.CONTENT_TYPE, "application/json")
            .submit(jsonBody)) {
      assertThat(response.status()).isEqualTo(Status.CREATED_201);
      assertThat(response.entity().as(String.class))
          .isEqualTo(
              "{\"metrics\":{\"http_req_failed\":{\"type\":\"rate\",\"contains\":\"default\",\"values\":{\"rate\":0.0,\"passes\":0.0,\"fails\":100.0}},\"data_received\":{\"type\":\"counter\",\"contains\":\"data\",\"values\":{\"count\":10600.0,\"rate\":1058.5548080483122}},\"http_req_blocked\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":4.3165,\"min\":0.0,\"avg\":0.4172509999999999,\"med\":0.0,\"p(95)\":4.3165,\"p(90)\":0.49050000000003124}},\"http_req_receiving\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.6742,\"min\":0.0,\"avg\":0.027747,\"med\":0.0,\"p(95)\":0.053124999999998646,\"p(90)\":0.0}},\"iterations\":{\"type\":\"counter\",\"contains\":\"default\",\"values\":{\"count\":100.0,\"rate\":9.986366113663323}},\"data_sent\":{\"type\":\"counter\",\"contains\":\"data\",\"values\":{\"count\":8500.0,\"rate\":848.8411196613823}},\"iteration_duration\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1005.6491,\"min\":1000.3286,\"avg\":1001.3614669999998,\"med\":1000.9534,\"p(95)\":1005.08385,\"p(90)\":1001.57965}},\"http_req_sending\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.5284,\"min\":0.0,\"avg\":0.026425999999999998,\"med\":0.0,\"p(95)\":0.1374,\"p(90)\":0.0}},\"http_req_duration\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1.5789,\"min\":0.0,\"avg\":0.5493939999999998,\"med\":0.6349,\"p(95)\":1.0441,\"p(90)\":0.6742}},\"http_req_tls_handshaking\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.0,\"min\":0.0,\"avg\":0.0,\"med\":0.0,\"p(95)\":0.0,\"p(90)\":0.0}},\"vus_max\":{\"type\":\"gauge\",\"contains\":\"default\",\"values\":{\"value\":10.0,\"min\":10.0,\"max\":10.0}},\"http_req_waiting\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1.0505,\"min\":0.0,\"avg\":0.4952209999999996,\"med\":0.5302,\"p(95)\":0.691554999999999,\"p(90)\":0.6742}},\"http_req_connecting\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.5284,\"min\":0.0,\"avg\":0.02642,\"med\":0.0,\"p(95)\":0.026419999999998497,\"p(90)\":0.0}},\"vus\":{\"type\":\"gauge\",\"contains\":\"default\",\"values\":{\"value\":10.0,\"min\":10.0,\"max\":10.0}},\"http_reqs\":{\"type\":\"counter\",\"contains\":\"default\",\"values\":{\"count\":100.0,\"rate\":9.986366113663323}}}}");
    }
  }

  @Test
  void shouldReturn400WhenNoHeaderInPost() throws JsonProcessingException {
    Map<String, Object> jsonBody = mapper.readValue(reportRequestBody, new TypeReference<>() {});

    try (Http1ClientResponse response =
                 client
                         .post("/metrics")
                         .header(HeaderNames.CONTENT_TYPE, "application/json")
                         .submit(jsonBody)) {
      assertThat(response.status()).isEqualTo(Status.BAD_REQUEST_400);
    }
  }

  String reportRequestBody =
"""
{
    "options": {
        "summaryTrendStats": [
            "avg",
            "min",
            "med",
            "max",
            "p(90)",
            "p(95)"
        ],
        "summaryTimeUnit": "",
        "noColor": false
    },
    "state": {
        "isStdOutTTY": true,
        "isStdErrTTY": true,
        "testRunDurationMs": 10013.6525
    },
    "metrics": {
        "http_req_failed": {
            "type": "rate",
            "contains": "default",
            "values": {
                "rate": 0,
                "passes": 0,
                "fails": 100
            }
        },
        "data_received": {
            "type": "counter",
            "contains": "data",
            "values": {
                "count": 10600,
                "rate": 1058.5548080483122
            }
        },
        "http_req_blocked": {
            "type": "trend",
            "contains": "time",
            "values": {
                "avg": 0.4172509999999999,
                "min": 0,
                "med": 0,
                "max": 4.3165,
                "p(90)": 0.49050000000003124,
                "p(95)": 4.3165
            }
        },
        "http_req_receiving": {
            "type": "trend",
            "contains": "time",
            "values": {
                "p(90)": 0,
                "p(95)": 0.053124999999998646,
                "avg": 0.027747,
                "min": 0,
                "med": 0,
                "max": 0.6742
            }
        },
        "iterations": {
            "contains": "default",
            "values": {
                "count": 100,
                "rate": 9.986366113663323
            },
            "type": "counter"
        },
        "data_sent": {
            "contains": "data",
            "values": {
                "rate": 848.8411196613823,
                "count": 8500
            },
            "type": "counter"
        },
        "iteration_duration": {
            "type": "trend",
            "contains": "time",
            "values": {
                "med": 1000.9534,
                "max": 1005.6491,
                "p(90)": 1001.57965,
                "p(95)": 1005.08385,
                "avg": 1001.3614669999998,
                "min": 1000.3286
            }
        },
        "http_req_sending": {
            "values": {
                "max": 0.5284,
                "p(90)": 0,
                "p(95)": 0.1374,
                "avg": 0.026425999999999998,
                "min": 0,
                "med": 0
            },
            "type": "trend",
            "contains": "time"
        },
        "http_req_duration": {
            "type": "trend",
            "contains": "time",
            "values": {
                "max": 1.5789,
                "p(90)": 0.6742,
                "p(95)": 1.0441,
                "avg": 0.5493939999999998,
                "min": 0,
                "med": 0.6349
            }
        },
        "http_req_tls_handshaking": {
            "values": {
                "min": 0,
                "med": 0,
                "max": 0,
                "p(90)": 0,
                "p(95)": 0,
                "avg": 0
            },
            "type": "trend",
            "contains": "time"
        },
        "vus_max": {
            "type": "gauge",
            "contains": "default",
            "values": {
                "value": 10,
                "min": 10,
                "max": 10
            }
        },
        "http_req_waiting": {
            "type": "trend",
            "contains": "time",
            "values": {
                "avg": 0.4952209999999996,
                "min": 0,
                "med": 0.5302,
                "max": 1.0505,
                "p(90)": 0.6742,
                "p(95)": 0.691554999999999
            }
        },
        "http_req_connecting": {
            "contains": "time",
            "values": {
                "avg": 0.02642,
                "min": 0,
                "med": 0,
                "max": 0.5284,
                "p(90)": 0,
                "p(95)": 0.026419999999998497
            },
            "type": "trend"
        },
        "vus": {
            "type": "gauge",
            "contains": "default",
            "values": {
                "min": 10,
                "max": 10,
                "value": 10
            }
        },
        "http_reqs": {
            "type": "counter",
            "contains": "default",
            "values": {
                "count": 100,
                "rate": 9.986366113663323
            }
        }
    },
    "root_group": {
        "groups": [],
        "checks": [],
        "name": "",
        "path": "",
        "id": "d41d8cd98f00b204e9800998ecf8427e"
    }
}
""";
}
