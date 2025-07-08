package com.blog.afaq.service;

import com.blog.afaq.dto.response.MonthlyAccessStat;
import com.blog.afaq.repository.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AccessLogRepository accessLogRepository;

    public long getTotalVisitorCount() {
        return accessLogRepository.count();
    }
    public List<MonthlyAccessStat> getVisitorTrafficLastSixMonths() {
        Instant sixMonthsAgo = Instant.now().minus(180, ChronoUnit.DAYS);
        return accessLogRepository.countMonthlyAccesses(sixMonthsAgo);
    }

    public Map<String, Object> getTotalVisitorsWithMonthlyChange() {
        Instant now = Instant.now();
        LocalDateTime startOfThisMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);

        long thisMonthCount = accessLogRepository.countByAccessTimeBetween(
                startOfThisMonth.atZone(ZoneId.systemDefault()).toInstant(),
                now
        );

        long lastMonthCount = accessLogRepository.countByAccessTimeBetween(
                startOfLastMonth.atZone(ZoneId.systemDefault()).toInstant(),
                startOfThisMonth.atZone(ZoneId.systemDefault()).toInstant()
        );

        long total = accessLogRepository.count();

        double change = 0;
        if (lastMonthCount > 0) {
            change = ((double) (thisMonthCount - lastMonthCount) / lastMonthCount) * 100;
        }

        String changeText = String.format(
                "%s%.0f%% par rapport au mois dernier",
                change >= 0 ? "+" : "",
                change
        );

        Map<String, Object> response = new HashMap<>();
        response.put("totalVisitors", total);
        response.put("thisMonth", thisMonthCount);
        response.put("lastMonth", lastMonthCount);
        response.put("change", changeText);

        return response;
    }


}
