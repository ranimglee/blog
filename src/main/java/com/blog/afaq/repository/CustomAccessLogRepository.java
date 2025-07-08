package com.blog.afaq.repository;

import com.blog.afaq.dto.response.MonthlyAccessStat;

import java.time.Instant;
import java.util.List;

public interface CustomAccessLogRepository {
    List<MonthlyAccessStat> countMonthlyAccesses(Instant fromDate);
}
