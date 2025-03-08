package com.helidon.startup;

import com.helidon.adapter.in.rest.CreateMetricsHandler;
import com.helidon.adapter.in.rest.DelegatingService;
import com.helidon.adapter.in.rest.GetMetricsHandler;
import com.helidon.adapter.in.rest.Mapper;
import com.helidon.adapter.out.MetricJDBCRepository;
import com.helidon.application.domain.service.MetricService;
import io.helidon.config.Config;
import io.helidon.http.media.MediaContext;
import io.helidon.http.media.MediaContextConfig;
import io.helidon.http.media.MediaSupport;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelidonWebserver {

  public static Logger LOG = LoggerFactory.getLogger(HelidonWebserver.class);

  public static void setup() {
    Mapper mapper = new Mapper();
    DataSource dataSource =
        DataSourceInstance.getDataSource(
            "jdbc:postgresql://localhost:5432/helidon", "user", "password");

    MetricJDBCRepository repository = new MetricJDBCRepository(dataSource);
    MetricService metricService = new MetricService(repository, repository);
    CreateMetricsHandler createMetricsHandler = new CreateMetricsHandler(metricService, mapper);
    GetMetricsHandler getMetricsHandler = new GetMetricsHandler(metricService, mapper);

    DelegatingService delegatingService =
        new DelegatingService(createMetricsHandler, getMetricsHandler);

    Config config = Config.create();

    WebServer.builder()
        .mediaContext(getMediaContext(config))
        .config(config.get("server"))
        .routing(HttpRouting.builder().register("/metrics", delegatingService))
        .build()
        .start();

    LOG.debug("HelidonWebserver started");
  }

  private static MediaContext getMediaContext(Config config) {
    MediaSupport jacksonSupport = JacksonSupport.create(config);
    return MediaContextConfig.builder().addMediaSupport(jacksonSupport).build();
  }
}
