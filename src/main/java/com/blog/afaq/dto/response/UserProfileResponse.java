package com.blog.afaq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String country;

}
