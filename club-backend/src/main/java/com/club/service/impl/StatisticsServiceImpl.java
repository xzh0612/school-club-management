package com.club.service.impl;

import com.club.entity.StatisticsData;
import com.club.entity.GrowthData;
import com.club.entity.ClubRanking;
import com.club.mapper.UserMapper;
import com.club.mapper.ClubMapper;
import com.club.mapper.ActivityMapper;
import com.club.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final UserMapper userMapper;
    private final ClubMapper clubMapper;
    private final ActivityMapper activityMapper;
    
    @Override
    public StatisticsData getOverviewStats() {
        return buildOverviewStats(null);
    }

    @Override
    public StatisticsData getOverviewStatsForAdvisor(Integer advisorId) {
        return buildOverviewStats(advisorId);
    }

    private StatisticsData buildOverviewStats(Integer advisorId) {
        StatisticsData stats = new StatisticsData();
        
        boolean advisorScoped = advisorId != null;
        stats.setTotalUsers((long) (advisorScoped ? userMapper.countStudentsInAdvisorClubs(advisorId) : userMapper.countAll()));
        
        stats.setActiveUsers((long) (advisorScoped ? userMapper.countActiveStudentsInAdvisorClubs(advisorId) : userMapper.countActiveStudentsInClubs()));
        
        stats.setTotalClubs((long) (advisorScoped ? clubMapper.countByAdvisor(advisorId) : clubMapper.countAll()));
        
        stats.setTotalActivities((long) (advisorScoped ? activityMapper.countByAdvisor(advisorId) : activityMapper.countAll()));
        stats.setCompletedActivities((long) (advisorScoped ? activityMapper.countByAdvisorAndStatus(advisorId, "completed") : activityMapper.countByStatus("completed")));
        
        int approvedClubs = advisorScoped ? clubMapper.countByAdvisorAndStatus(advisorId, "approved") : clubMapper.countByStatus("approved");
        int activeClubs = advisorScoped ? activityMapper.countAdvisorClubsWithApprovedActivities(advisorId) : activityMapper.countClubsWithApprovedActivities();
        double clubActivityRate = approvedClubs > 0 ?
            (double) activeClubs / approvedClubs * 100 : 0;
        stats.setClubActivityRate(Math.round(clubActivityRate * 10) / 10.0);
        
        // 计算活动完成率
        double activityCompletionRate = stats.getTotalActivities() > 0 ? 
            (double) stats.getCompletedActivities() / stats.getTotalActivities() * 100 : 0;
        stats.setActivityCompletionRate(Math.round(activityCompletionRate * 10) / 10.0);
        
        int totalStudents = advisorScoped ? userMapper.countStudentsInAdvisorClubs(advisorId) : userMapper.countByRole("student");
        double participationRate = totalStudents > 0 ?
            Math.min(100.0, (double) stats.getActiveUsers() / totalStudents * 100) : 0;
        stats.setStudentParticipationRate(Math.round(participationRate * 10) / 10.0);
        
        return stats;
    }
    
    @Override
    public List<GrowthData> getClubGrowthTrend() {
        return buildGrowthTrend(clubMapper.countCreatedByYear());
    }

    @Override
    public List<GrowthData> getClubGrowthTrendForAdvisor(Integer advisorId) {
        return buildGrowthTrend(clubMapper.countCreatedByYearForAdvisor(advisorId));
    }

    private List<GrowthData> buildGrowthTrend(List<java.util.Map<String, Object>> rows) {
        List<GrowthData> growthData = new ArrayList<>();

        int total = rows.stream()
                .mapToInt(row -> ((Number) row.get("count")).intValue())
                .sum();
        int cumulative = 0;
        int currentYear = Year.now().getValue();

        for (java.util.Map<String, Object> row : rows) {
            int year = ((Number) row.get("year")).intValue();
            cumulative += ((Number) row.get("count")).intValue();
            GrowthData data = new GrowthData();
            data.setYear(String.valueOf(year));
            data.setCount(cumulative);
            int pct = total > 0 ? (int) Math.round((cumulative * 100.0) / total) : 0;
            data.setPercentage(pct + "%");
            data.setCurrent(year == currentYear);
            growthData.add(data);
        }
        
        return growthData;
    }
    
    @Override
    public List<ClubRanking> getClubRankings() {
        return buildRankings(activityMapper.clubRankingStats());
    }

    @Override
    public List<ClubRanking> getClubRankingsForAdvisor(Integer advisorId) {
        return buildRankings(activityMapper.clubRankingStatsForAdvisor(advisorId));
    }

    private List<ClubRanking> buildRankings(List<java.util.Map<String, Object>> rows) {
        List<ClubRanking> rankings = new ArrayList<>();
        
        for (int i = 0; i < rows.size(); i++) {
            java.util.Map<String, Object> row = rows.get(i);
            int events = ((Number) row.get("activity_count")).intValue();
            int members = ((Number) row.get("member_count")).intValue();
            ClubRanking ranking = new ClubRanking();
            ranking.setRank(i + 1);
            ranking.setName((String) row.get("club_name"));
            ranking.setEvents(events);
            ranking.setMembers(members);
            rankings.add(ranking);
        }
        
        return rankings;
    }
}
