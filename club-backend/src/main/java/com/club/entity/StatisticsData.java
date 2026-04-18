package com.club.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsData {
    private Long totalUsers;        // 总用户数
    private Long activeUsers;       // 活跃用户数
    private Double clubActivityRate; // 社团活跃率
    private Double activityCompletionRate; // 活动完成率
    private Double studentParticipationRate; // 学生参与率
    private Long totalClubs;        // 总社团数
    private Long totalActivities;   // 总活动数
    private Long completedActivities; // 已完成活动数
}