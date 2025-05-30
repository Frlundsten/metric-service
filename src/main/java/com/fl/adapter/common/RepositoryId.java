package com.fl.adapter.common;

import java.util.Objects;

public record RepositoryId(String value) {
  public static final ScopedValue<RepositoryId> REPOSITORY_ID = ScopedValue.newInstance();

  public RepositoryId {
    Objects.requireNonNull(value, "Repository id cannot be null");
    if (value.isEmpty()) {
      throw new IllegalArgumentException("Repository id cannot be empty");
    }
  }

  public static RepositoryId getScopedValue() {
    return REPOSITORY_ID.get();
  }
}
