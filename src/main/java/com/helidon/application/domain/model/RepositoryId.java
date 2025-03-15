package com.helidon.application.domain.model;

public record RepositoryId(String value) {
  public static final ScopedValue<RepositoryId> REPOSITORY_ID = ScopedValue.newInstance();

  public RepositoryId {
    if (value == null) {
      throw new IllegalArgumentException("Repository id cannot be null");
    }
  }

  public static RepositoryId getScopedValue() {
    return REPOSITORY_ID.get();
  }
}
