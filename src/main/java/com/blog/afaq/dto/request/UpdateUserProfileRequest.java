package com.blog.afaq.dto.request;


public record UpdateUserProfileRequest(
        String firstname,
        String lastname,
        String phoneNumber,
        String country

) {
}
