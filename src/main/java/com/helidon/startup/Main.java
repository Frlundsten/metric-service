package com.helidon.startup;

import com.helidon.adapter.in.rest.CreateMetricsHandler;
import com.helidon.adapter.in.rest.DelegatingService;
import com.helidon.adapter.in.rest.GetMetricsHandler;
import com.helidon.adapter.out.MetricJDBCRepository;
import com.helidon.application.domain.service.MetricService;
import com.helidon.util.Mapper;
import javax.sql.DataSource;

public class Main {
  public static void main(String[] args) {
    Mapper mapper = new Mapper();
    DataSource dataSource =
        DataSourceInstance.getDataSource(
            "jdbc:postgresql://localhost:5432/helidon", "user", "password");

    MetricJDBCRepository repository = new MetricJDBCRepository(dataSource);
    MetricService metricService = new MetricService(repository, repository);
    CreateMetricsHandler createMetricsHandler = new CreateMetricsHandler(metricService, mapper);
    GetMetricsHandler getMetricsHandler = new GetMetricsHandler(metricService);

    DelegatingService delegatingService =
        new DelegatingService(createMetricsHandler, getMetricsHandler);
    HelidonWebserver.setup(delegatingService);
  }
}
