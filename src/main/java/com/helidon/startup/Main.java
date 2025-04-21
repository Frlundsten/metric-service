package com.helidon.startup;

import com.helidon.adapter.in.rest.CreateMetricsHandler;
import com.helidon.adapter.in.rest.DelegatingService;
import com.helidon.adapter.in.rest.GetMetricsHandler;
import com.helidon.adapter.out.MetricJDBCRepository;
import com.helidon.application.domain.service.MetricService;
import com.helidon.util.Mapper;
import io.helidon.dbclient.DbClient;

import javax.sql.DataSource;

public class Main {
  public static void main(String[] args) {
    HelidonWebserver.setup();
  }
}
