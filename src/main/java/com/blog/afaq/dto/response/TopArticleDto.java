package com.blog.afaq.dto.response;

import lombok.Data;

@Data
public class TopArticleDto {
    private String articleId;
    private String title;
    private long views;
}