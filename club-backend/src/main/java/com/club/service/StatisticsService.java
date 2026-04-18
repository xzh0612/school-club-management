package com.club.service;

import com.club.entity.StatisticsData;
import com.club.entity.GrowthData;
import com.club.entity.ClubRanking;
import java.util.List;

public interface StatisticsService {
    StatisticsData getOverviewStats();
    List<GrowthData> getClubGrowthTrend();
    List<ClubRanking> getClubRankings();
}