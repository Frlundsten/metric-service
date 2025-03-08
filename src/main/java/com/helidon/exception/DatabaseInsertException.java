package com.helidon.exception;

public class DatabaseInsertException extends RuntimeException {
  public DatabaseInsertException(String message) {
    super(message);
  }
}
