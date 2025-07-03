package com.blog.afaq.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CommentResponse {
    private String id;
    private String content;
    private String author;
    private boolean approved;
    private Date createdAt;
}
