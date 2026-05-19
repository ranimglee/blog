package com.blog.afaq.dto.response;

public record SessionDto(
        String sessionId,
        String visitorId,
        long pageViews,
        long durationSeconds
) {}