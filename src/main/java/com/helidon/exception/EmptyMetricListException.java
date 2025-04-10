package com.helidon.exception;

public class EmptyMetricListException extends RuntimeException {
  public EmptyMetricListException(String message) {
    super(message);
  }
}
