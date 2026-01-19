package com.blog.afaq.dto.response;

import com.blog.afaq.model.Role;
import com.blog.afaq.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
@Data
@AllArgsConstructor
public class UserRegisterResponse {
    private String firstname;
    private String lastname;
    private String email;
    private String country;
    private String phoneNumber;
    private UserStatus status;
    private Instant createdAt;
    private Role role;
}
