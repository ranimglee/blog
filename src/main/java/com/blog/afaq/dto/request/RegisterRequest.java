package com.blog.afaq.dto.request;

import lombok.Data;

import java.time.Instant;

@Data
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String country;
    private String phoneNumber;

}