package com.springweb.employeemanagement.advice.customexceptions;

public class InvalidOperationException extends RuntimeException {
  public InvalidOperationException(String message) {
    super(message);
  }
}
