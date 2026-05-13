package com.club.service;

import com.club.entity.StatisticsData;
import com.club.entity.GrowthData;
import com.club.entity.ClubRanking;
import java.util.List;

public interface StatisticsService {
    StatisticsData getOverviewStats();
    StatisticsData getOverviewStatsForAdvisor(Integer advisorId);
    List<GrowthData> getClubGrowthTrend();
    List<GrowthData> getClubGrowthTrendForAdvisor(Integer advisorId);
    List<ClubRanking> getClubRankings();
    List<ClubRanking> getClubRankingsForAdvisor(Integer advisorId);
}
