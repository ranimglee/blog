package com.blog.afaq.dto.request;

import com.blog.afaq.model.Language;
import lombok.Data;

import java.time.Instant;

@Data
public class InitiativeRequest {
    private String title;
    private String subTitle;
    private String content;
    private String imageUrl;
    private String country;
    private Language language;
}