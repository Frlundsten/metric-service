package com.helidon.application.service;

import static org.mockito.Mockito.verify;

import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @Mock Repository repository;
  @Mock Metrics metrics;
  @InjectMocks PostService postService;

  @Test
  void shouldCallRepository() {
    postService.saveMetrics(metrics);
    verify(repository).save(metrics);
  }
}
