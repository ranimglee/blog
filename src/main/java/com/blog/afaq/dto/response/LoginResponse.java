package com.blog.afaq.dto.response;

import com.blog.afaq.model.Role;

public record LoginResponse(String token, String refreshToken, Role role) {
}
