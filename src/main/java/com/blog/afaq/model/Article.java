package com.blog.afaq.model;

import com.blog.afaq.model.ArticleType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {
    @Id
    private String id;

    private String title;
    private Date createdAt;
    private String description;
    private String auteur;
    private ArticleType type;
    private String contenu;
    private String imageUrl;
    private Language language;

}
