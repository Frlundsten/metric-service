package com.helidon.application.service;

import com.helidon.application.domain.model.Metrics;
import com.helidon.application.domain.service.Service;
import com.helidon.application.port.out.Repository;

public class PostService implements Service {

  private final Repository repository;

  public PostService(Repository repository) {
    this.repository = repository;
  }

  @Override
  public void saveMetrics(Metrics metrics) {
    repository.save(metrics);
  }
}
