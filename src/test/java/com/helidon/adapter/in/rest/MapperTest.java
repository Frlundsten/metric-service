package com.helidon.adapter.in.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class MapperTest {
  ObjectMapper jacksonMapper;
  Mapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new Mapper();
    jacksonMapper = new ObjectMapper();
  }

  @Test
  void toWantedK6Metrics() throws JsonProcessingException {
    var mapOfMetrics = jacksonMapper.readValue(request, Map.class);

    var e  = mapper.fromDtoMap(mapOfMetrics);
  }

  String request =
      """
  {
    "http_req_failed": {
      "type": "rate",
      "contains": "default",
      "values": {
        "rate": 0,
        "passes": 0,
        "fails": 100
      }
    },
     "undercover": {
      "type": "unwanted",
      "contains": "unknown",
      "values": {
        "count": 666,
        "rate": 123.5548080483122
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
  }
""";

  //  @Test
  //  void shouldMapToK6Metrics() throws JsonProcessingException {
  //
  //    var wantedCount = WantedK6Metrics.values().length;
  //    var dto = jacksonMapper.readValue(request, Map.class);
  //    var metrics = mapper.fromDTO(dto);
  //
  //    assertThat(metrics.metricList()).hasSize(wantedCount);
  //  }
}
