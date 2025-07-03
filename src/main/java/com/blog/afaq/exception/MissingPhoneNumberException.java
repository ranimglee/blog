package com.blog.afaq.exception;

public class MissingPhoneNumberException extends RuntimeException {
  public MissingPhoneNumberException(String message) {
    super(message);
  }
}
