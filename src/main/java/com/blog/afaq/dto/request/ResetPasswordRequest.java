package com.blog.afaq.dto.request;

public record ResetPasswordRequest(String email, String code, String newPassword) {
}
