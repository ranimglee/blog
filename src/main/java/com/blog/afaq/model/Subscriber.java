package com.blog.afaq.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@Getter
@Setter
public class Subscriber {

    @Id
    private String id;

    private String email;

    private boolean confirmed;

    private boolean consentGiven ;
    private boolean subscribed = true;

    private String confirmationToken;

    private String unsubscribeToken;

    private Instant subscribedAt;
    private Instant confirmedAt;
    private Instant unsubscribedAt;
}