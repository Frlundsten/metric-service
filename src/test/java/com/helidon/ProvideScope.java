package com.helidon;

import com.helidon.adapter.RepositoryId;
import com.helidon.exception.ProvidedScopeException;


// Provide scoped value for tests
public class ProvideScope {
  public static void withScope(Runnable scopeForTest) {
    ScopedValue.where(RepositoryId.REPOSITORY_ID, new RepositoryId("test-repo")).run(scopeForTest);
  }
  public static String withScope(ScopedValue.CallableOp<String, ProvidedScopeException> scopeForTest) {
    return ScopedValue.where(RepositoryId.REPOSITORY_ID, new RepositoryId("test-repo")).call(scopeForTest);
  }
}
