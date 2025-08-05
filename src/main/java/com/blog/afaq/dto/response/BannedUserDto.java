package com.blog.afaq.dto.response;

import com.blog.afaq.model.Role;
import com.blog.afaq.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannedUserDto {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String country;
    private String phoneNumber;
    private UserStatus status;
    private Role role;
    private Instant createdAt;
    private Instant bannedAt;
}
