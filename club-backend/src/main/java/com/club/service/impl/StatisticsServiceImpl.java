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
        StatisticsData stats = new StatisticsData();
        
        // 获取总用户数
        stats.setTotalUsers((long) userMapper.countAll());
        
        stats.setActiveUsers((long) userMapper.countActiveStudentsInClubs());
        
        // 获取总社团数
        stats.setTotalClubs((long) clubMapper.countAll());
        
        // 获取总活动数和已完成活动数
        stats.setTotalActivities((long) activityMapper.countAll());
        stats.setCompletedActivities((long) activityMapper.countByStatus("completed"));
        
        int approvedClubs = clubMapper.countByStatus("approved");
        int activeClubs = activityMapper.countClubsWithApprovedActivities();
        double clubActivityRate = approvedClubs > 0 ?
            (double) activeClubs / approvedClubs * 100 : 0;
        stats.setClubActivityRate(Math.round(clubActivityRate * 10) / 10.0);
        
        // 计算活动完成率
        double activityCompletionRate = stats.getTotalActivities() > 0 ? 
            (double) stats.getCompletedActivities() / stats.getTotalActivities() * 100 : 0;
        stats.setActivityCompletionRate(Math.round(activityCompletionRate * 10) / 10.0);
        
        int totalStudents = userMapper.countByRole("student");
        double participationRate = totalStudents > 0 ?
            Math.min(100.0, (double) stats.getActiveUsers() / totalStudents * 100) : 0;
        stats.setStudentParticipationRate(Math.round(participationRate * 10) / 10.0);
        
        return stats;
    }
    
    @Override
    public List<GrowthData> getClubGrowthTrend() {
        List<GrowthData> growthData = new ArrayList<>();
        List<java.util.Map<String, Object>> rows = clubMapper.countCreatedByYear();

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
        List<ClubRanking> rankings = new ArrayList<>();
        
        List<java.util.Map<String, Object>> rows = activityMapper.clubRankingStats();
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
