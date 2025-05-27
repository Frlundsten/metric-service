package com.fl.exception;

public class DatabaseInsertException extends RuntimeException {
  public DatabaseInsertException(String message) {
    super(message);
  }

  public DatabaseInsertException(String message, Throwable cause) {
    super(message, cause);
  }
}
