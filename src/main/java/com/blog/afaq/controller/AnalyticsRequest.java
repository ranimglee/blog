package com.blog.afaq.controller;

public record AnalyticsRequest(

        String event,
        String path,

        String referrer,
        String pageId,
        String category,

        String userAgent

) {}