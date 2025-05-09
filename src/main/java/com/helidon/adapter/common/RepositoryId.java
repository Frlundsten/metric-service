package com.helidon.adapter.common;

import java.util.Objects;

public record RepositoryId(String value) {
  public static final ScopedValue<RepositoryId> REPOSITORY_ID = ScopedValue.newInstance();

  public RepositoryId {
    if (value.isEmpty()) {
      throw new IllegalArgumentException("Repository id cannot be empty");
    }
    Objects.requireNonNull(value, "Repository id cannot be null");
  }

  public static RepositoryId getScopedValue() {
    return REPOSITORY_ID.get();
  }
}
