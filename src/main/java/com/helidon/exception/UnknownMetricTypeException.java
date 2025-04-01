package com.helidon.exception;

public class UnknownMetricTypeException extends RuntimeException {
  public UnknownMetricTypeException(String message) {
    super(message);
  }
}
