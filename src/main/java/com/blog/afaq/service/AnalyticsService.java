package com.blog.afaq.service;

import com.blog.afaq.dto.request.AnalyticsRequest;
import com.blog.afaq.dto.response.*;
import com.blog.afaq.model.AccessLog;
import com.blog.afaq.model.Role;
import com.blog.afaq.repository.AccessLogRepository;
import com.blog.afaq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AccessLogRepository accessLogRepository;
    private final UserRepository userRepository;



    // ----------------------------
    // USERS
    // ----------------------------
    public List<UserDto> getAllNonAdminUsers() {
        return userRepository.findByRole(Role.USER);
    }

    public AnalyticsDashboardDto getDashboard() {

        Instant now = Instant.now();

        Instant today = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant thisMonthStart = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant lastMonthStart = LocalDate.now()
                .minusMonths(1)
                .withDayOfMonth(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        long total = Optional.ofNullable(
                accessLogRepository.countUniqueVisitors()
        ).orElse(0L);

        long todayVisitors = Optional.ofNullable(
                accessLogRepository.countUniqueVisitorsBetween(today, now)
        ).orElse(0L);

        long activeSessions = Optional.ofNullable(
                accessLogRepository.countActiveSessions(today)
        ).orElse(0L);

        long thisMonth = Optional.ofNullable(
                accessLogRepository.countUniqueVisitorsBetween(
                        thisMonthStart,
                        now
                )
        ).orElse(0L);

        long lastMonth = Optional.ofNullable(
                accessLogRepository.countUniqueVisitorsBetween(
                        lastMonthStart,
                        thisMonthStart
                )
        ).orElse(0L);

        double change = lastMonth == 0
                ? 0
                : ((double) (thisMonth - lastMonth) / lastMonth) * 100;

        return new AnalyticsDashboardDto(
                total,
                todayVisitors,
                thisMonth,
                lastMonth,
                change,
                activeSessions
        );
    }
    // ----------------------------
    // TRACK EVENT
    // ----------------------------
    public void trackEvent(AnalyticsRequest request, String visitorId, String sessionId) {

        if (request == null || request.path() == null || request.path().isBlank()) {
            return;
        }

        String path = request.path();

        if (path.startsWith("/api/analytics")) return;
        if (path.startsWith("/api/auth")) return;

        AccessLog log = new AccessLog();
        log.setVisitorId(visitorId);
        log.setSessionId(sessionId);
        log.setPath(path);
        log.setEvent(request.event());

        log.setReferrer(request.referrer());
        log.setPageId(request.pageId());
        log.setCategory(request.category());
        log.setUserAgent(request.userAgent());
        log.setDeviceType(detectDeviceType(request.userAgent()));
        log.setAccessTime(Instant.now());

        accessLogRepository.save(log);
    }

    // ----------------------------
    // TRAFFIC / TOP QUERIES
    // ----------------------------
    public List<TrafficPerDayDto> getTrafficPerDay(Instant since) {
        return accessLogRepository.getTrafficPerDay(since);
    }

    public List<TopPageDto> getTopPages(int limit) {
        return accessLogRepository.getTopPages(limit);
    }

    public List<TopArticleDto> getTopArticles() {
        return accessLogRepository.getTopArticles();
    }

    public List<UserAgentStatsDto> getUserAgentStats() {
        return accessLogRepository.getUserAgentStats();
    }

    private String detectDeviceType(String userAgent) {
        if (userAgent == null) return "Other";

        String ua = userAgent.toLowerCase();

        // 1. Mobile FIRST (highest priority)
        if (ua.contains("android") || ua.contains("iphone") || ua.contains("mobile")) {
            return "Mobile";
        }

        // 2. Tablet
        if (ua.contains("ipad") || ua.contains("tablet")) {
            return "Tablet";
        }

        // 3. Desktop (be careful: Linux ≠ always desktop)
        if (ua.contains("windows") || ua.contains("macintosh")) {
            return "Desktop";
        }

        return "Other";
    }
    public List<TopProjectDto> getTopProjects() {
        return accessLogRepository.getTopProjects();
    }
}