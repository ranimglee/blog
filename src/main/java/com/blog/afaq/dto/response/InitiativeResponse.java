package com.blog.afaq.dto.response;

import lombok.Data;

import java.time.Instant;

@Data
public class InitiativeResponse {
    private String id;
    private String title;
    private String subTitle;
    private String content;
    private String imageUrl;
    private String country;
    private Instant createdAt;
}