package com.blog.afaq.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String country;
    private String phoneNumber;
    private UserStatus status;
    private String refreshToken;
    private Instant createdAt;
    private Role role;
    private Instant bannedAt;

}
