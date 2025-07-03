package com.blog.afaq.exception;

public class InvalidResetCodeException extends RuntimeException {
    public InvalidResetCodeException(String message) {
        super(message);
    }
}
