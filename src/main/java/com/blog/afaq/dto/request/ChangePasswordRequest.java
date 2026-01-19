package com.blog.afaq.dto.request;

public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
