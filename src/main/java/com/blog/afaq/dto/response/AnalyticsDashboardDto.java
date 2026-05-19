package com.blog.afaq.dto.response;

public record AnalyticsDashboardDto(
        long totalVisitors,
        long todayVisitors,
        long thisMonthVisitors,
        long lastMonthVisitors,
        double changePercent,
        long activeSessions
) {}