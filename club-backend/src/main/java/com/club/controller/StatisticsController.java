package com.club.controller;

import com.club.common.Result;
import com.club.entity.StatisticsData;
import com.club.entity.GrowthData;
import com.club.entity.ClubRanking;
import com.club.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping("/overview")
    public Result<StatisticsData> getOverviewStats() {
        return Result.ok(statisticsService.getOverviewStats());
    }
    
    @GetMapping("/growth-trend")
    public Result<List<GrowthData>> getGrowthTrend() {
        return Result.ok(statisticsService.getClubGrowthTrend());
    }
    
    @GetMapping("/rankings")
    public Result<List<ClubRanking>> getClubRankings() {
        return Result.ok(statisticsService.getClubRankings());
    }
    
    @GetMapping("/all")
    public Result<?> getAllStatistics() {
        StatisticsData overview = statisticsService.getOverviewStats();
        List<GrowthData> growthTrend = statisticsService.getClubGrowthTrend();
        List<ClubRanking> rankings = statisticsService.getClubRankings();
        
        return Result.ok(new Object() {
            public StatisticsData stats = overview;
            public List<GrowthData> growth = growthTrend;
            public List<ClubRanking> ranking = rankings;
        });
    }
}