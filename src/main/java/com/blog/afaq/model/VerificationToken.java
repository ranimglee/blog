package com.blog.afaq.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "verification_token")

public class VerificationToken {
    @Id
    private String id;
    private String token;
    private String userId;
    private LocalDateTime expiresAt;
}