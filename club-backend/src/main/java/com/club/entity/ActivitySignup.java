package com.club.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySignup {
    private Integer signupId;        // 报名ID
    private Integer activityId;      // 活动ID
    private Integer userId;          // 用户ID
    private LocalDateTime signupTime;    // 报名时间
    private String status;           // 状态: pending, approved, rejected, attended, absent
    private LocalDateTime checkinTime;   // 签到时间
    
    // 关联字段
    private String activityTitle;    // 活动标题
    private String userName;         // 用户姓名
    private String userSid;          // 学号
}