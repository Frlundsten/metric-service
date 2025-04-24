package com.helidon;

import com.helidon.adapter.RepositoryId;
import com.helidon.exception.ProvidedScopeException;


// Provide scoped value for tests
public class ProvideScope {
  public static void withScope(Runnable scopeForTest) {
    ScopedValue.where(RepositoryId.REPOSITORY_ID, new RepositoryId("test-repo")).run(scopeForTest);
  }
  public static <T> T withScope(ScopedValue.CallableOp<T, ProvidedScopeException> scopeForTest) {
    return ScopedValue.where(RepositoryId.REPOSITORY_ID, new RepositoryId("test-repo")).call(scopeForTest);
  }
}
