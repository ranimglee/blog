package com.blog.afaq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String token;
    private String errorCode;
    private Object data;

    // ✅ Constructor for error responses
    public AuthResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.token = null;
        this.data = null;
    }

    // ✅ Constructor for success responses (login)
    public AuthResponse(String message, String token, Object data) {
        this.message = message;
        this.token = token;
        this.data = data;
        this.errorCode = null;
    }
}