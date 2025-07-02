package com.blog.afaq.dto.response;

import com.blog.afaq.model.ArticleType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ArticleResponse {
    private String id;
    private String title;
    private String description;
    private String auteur;
    private ArticleType type;
    private String contenu;
    private Date createdAt;
    private String imageUrl;

}
