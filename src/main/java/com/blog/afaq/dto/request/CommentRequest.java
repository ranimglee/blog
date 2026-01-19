package com.blog.afaq.dto.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String articleId;
    private String content;
}

