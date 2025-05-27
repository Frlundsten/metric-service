package com.fl.exception;

public class MetricNameValidationException extends RuntimeException {
  public MetricNameValidationException(String message) {
    super(message);
  }
}
