package com.fl.exception;

public class EmptyMetricListException extends RuntimeException {
  public EmptyMetricListException(String message) {
    super(message);
  }
}
