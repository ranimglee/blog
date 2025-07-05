package com.blog.afaq.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Document
@Getter
@Setter
public class Subscriber {
    @Id
    private String id;

    private String email;
    private boolean confirmed = false; // Double opt-in confirmation

    private String confirmationToken;

    private Instant subscribedAt;

    private boolean consentGiven;

    private Instant confirmedAt;


}
