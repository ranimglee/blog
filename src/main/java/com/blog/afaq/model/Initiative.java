package com.blog.afaq.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "initiatives")
public class Initiative {
    @Id
    private String id;
    private String title;
    private String subTitle;
    private String content;
    private String imageUrl;
    private String country;
    private Instant createdAt;
    private Language language;
    private Instant updatedAt;

}
