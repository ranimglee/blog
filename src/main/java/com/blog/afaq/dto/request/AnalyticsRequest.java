package com.blog.afaq.dto.request;

public record AnalyticsRequest(

        String event,
        String path,

        String referrer,
        String pageId,
        String category,

        String userAgent

) {}