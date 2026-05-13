package com.club.controller;

import com.club.common.Result;
import com.club.common.SecurityContext;
import com.club.entity.StatisticsData;
import com.club.entity.GrowthData;
import com.club.entity.ClubRanking;
import com.club.service.StatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    private final SecurityContext securityContext;
    
    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<StatisticsData> getOverviewStats(HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        if (securityContext.isTeacher(request)) {
            return Result.ok(statisticsService.getOverviewStatsForAdvisor(securityContext.currentUserId(request)));
        }
        return Result.ok(statisticsService.getOverviewStats());
    }
    
    @GetMapping("/growth-trend")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<List<GrowthData>> getGrowthTrend(HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        if (securityContext.isTeacher(request)) {
            return Result.ok(statisticsService.getClubGrowthTrendForAdvisor(securityContext.currentUserId(request)));
        }
        return Result.ok(statisticsService.getClubGrowthTrend());
    }
    
    @GetMapping("/rankings")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<List<ClubRanking>> getClubRankings(HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        if (securityContext.isTeacher(request)) {
            return Result.ok(statisticsService.getClubRankingsForAdvisor(securityContext.currentUserId(request)));
        }
        return Result.ok(statisticsService.getClubRankings());
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<?> getAllStatistics(HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        boolean advisorScoped = securityContext.isTeacher(request);
        Integer advisorId = advisorScoped ? securityContext.currentUserId(request) : null;
        StatisticsData overview = advisorScoped ? statisticsService.getOverviewStatsForAdvisor(advisorId) : statisticsService.getOverviewStats();
        List<GrowthData> growthTrend = advisorScoped ? statisticsService.getClubGrowthTrendForAdvisor(advisorId) : statisticsService.getClubGrowthTrend();
        List<ClubRanking> rankings = advisorScoped ? statisticsService.getClubRankingsForAdvisor(advisorId) : statisticsService.getClubRankings();
        
        return Result.ok(new Object() {
            public StatisticsData stats = overview;
            public List<GrowthData> growth = growthTrend;
            public List<ClubRanking> ranking = rankings;
        });
    }
}
