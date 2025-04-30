package com.helidon.startup;

import com.helidon.adapter.in.rest.CreateMetricsHandler;
import com.helidon.adapter.in.rest.DelegatingService;
import com.helidon.adapter.in.rest.ReportTimespanHandler;
import com.helidon.adapter.in.rest.RecentReportsHandler;
import com.helidon.adapter.out.MetricJDBCRepository;
import com.helidon.application.domain.service.MetricService;
import com.helidon.adapter.Mapper;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelidonWebserver {

  public static final Logger LOG = LoggerFactory.getLogger(HelidonWebserver.class);

  private HelidonWebserver() {}

  public static void setup() {
    Config config = Config.create();
    Mapper mapper = new Mapper();
    DbClient dbClient = DbClient.create(config.get("db"));

    MetricJDBCRepository repository = new MetricJDBCRepository(dbClient);
    MetricService metricService = new MetricService(repository, repository);
    CreateMetricsHandler createMetricsHandler = new CreateMetricsHandler(metricService, mapper);
    ReportTimespanHandler reportTimespanHandler = new ReportTimespanHandler(metricService);
    RecentReportsHandler recentReportshandler = new RecentReportsHandler(metricService);

    DelegatingService delegatingService =
        new DelegatingService(createMetricsHandler, reportTimespanHandler,recentReportshandler);

    WebServer.builder()
        .config(config.get("server"))
        .routing(HttpRouting.builder().register("/metrics", delegatingService))
        .build()
        .start();

    LOG.debug("HelidonWebserver started");
  }
}
