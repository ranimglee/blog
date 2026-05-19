package com.blog.afaq.dto.response;

public record TrafficPerDayDto(
        String date,
        long count
) {}