package com.blog.afaq.controller;

import com.blog.afaq.dto.response.MonthlyAccessStat;
import com.blog.afaq.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ArticleService articleService;
    private final InitiativeService initiativeService;
    private final RessourceService ressourceService;
    private final CommentService commentService;

    @GetMapping("/traffic")
    public List<MonthlyAccessStat> getTrafficStats() {
        return analyticsService.getVisitorTrafficLastSixMonths();
    }
    @GetMapping("/total-visitors")
    public long getTotalVisitors() {
        return analyticsService.getTotalVisitorCount(); // or getTotalUniqueVisitors()
    }
    @GetMapping("/count-articles")
    public long getTotalArticles() {
        return articleService.getTotalArticles();
    }
    @GetMapping("/total-visitors-stats")
    public Map<String, Object> getTotalVisitorsWithStats() {
        return analyticsService.getTotalVisitorsWithMonthlyChange();
    }
    @GetMapping("/count-initiatives")
    public long getTotalInitiaves() {
        return initiativeService.getTotalInitiatives();
    }
    @GetMapping("/count-ressources")
    public long getTotalResources() {
        return ressourceService.getTotalResources();
    }

    @GetMapping("/stats")
    public Map<String, Long> getCommentStatistics() {
        return commentService.getCommentStats();
    }

}
