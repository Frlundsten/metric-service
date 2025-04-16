package com.helidon;

import com.helidon.adapter.RepositoryId;

// Provide scoped value for tests
public class ProvideScope {
  public static void withScope(Runnable scopeForTest) {
    ScopedValue.where(RepositoryId.REPOSITORY_ID, new RepositoryId("test-repo")).run(scopeForTest);
  }
}
