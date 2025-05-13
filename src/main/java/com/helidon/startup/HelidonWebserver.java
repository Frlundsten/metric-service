package com.helidon.startup;

import com.helidon.adapter.common.Mapper;
import com.helidon.adapter.common.MetricReportTypeMapperProvider;
import com.helidon.adapter.common.MetricTypeMapperProvider;
import com.helidon.adapter.in.rest.CreateMetricsHandler;
import com.helidon.adapter.in.rest.DelegatingService;
import com.helidon.adapter.in.rest.RecentReportsHandler;
import com.helidon.adapter.in.rest.ReportTimespanHandler;
import com.helidon.adapter.out.mail.MailSender;
import com.helidon.adapter.out.persistence.MetricJDBCRepository;
import com.helidon.application.domain.service.MetricService;
import com.helidon.application.port.out.create.ForAlertingUser;
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
    DbClient client =
        DbClient.builder(config.get("db"))
            .mapperProvider(new MetricReportTypeMapperProvider())
            .mapperProvider(new MetricTypeMapperProvider())
            .build();

    DelegatingService delegatingService = getDelegatingService(client, mapper);

    WebServer.builder()
        .config(config.get("server"))
        .routing(getRouting(delegatingService))
        .build()
        .start();

    LOG.debug("HelidonWebserver started");
  }

  private static HttpRouting.Builder getRouting(DelegatingService delegatingService) {
    return HttpRouting.builder().register("/metrics", delegatingService);
  }

  private static DelegatingService getDelegatingService(DbClient client, Mapper mapper) {
    MetricJDBCRepository repository = new MetricJDBCRepository(client);
    ForAlertingUser alertUser = new MailSender();
    MetricService metricService = new MetricService(repository, repository, alertUser);
    CreateMetricsHandler createMetricsHandler = new CreateMetricsHandler(metricService, mapper);
    ReportTimespanHandler reportTimespanHandler = new ReportTimespanHandler(metricService);
    RecentReportsHandler recentReportshandler = new RecentReportsHandler(metricService);

    return new DelegatingService(createMetricsHandler, reportTimespanHandler, recentReportshandler);
  }

}
