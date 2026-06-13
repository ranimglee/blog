package com.blog.afaq.dto.response;

import lombok.Data;

@Data
public class TopProjectDto {
    private String projectId;

    private String title;
    private long views;
}