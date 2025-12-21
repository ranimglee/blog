package com.blog.afaq.exception;

public class ResetCodeDeliveryException extends RuntimeException {
    public ResetCodeDeliveryException(String message, Exception e) {
        super(message);
    }
}
