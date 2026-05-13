package com.club.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    private Integer activityId;    // 活动ID
    private Integer clubId;        // 社团ID
    private String title;          // 活动标题
    private String content;        // 活动详情
    private String type;           // 活动类型
    private Integer maxParticipants; // 最大参与人数
    private LocalDateTime registrationDeadline; // 报名截止时间
    private String organizer;      // 活动负责人
    private String contact;        // 联系方式
    private LocalDateTime startTime;   // 开始时间
    private LocalDateTime endTime;     // 结束时间
    private String location;       // 活动地点
    private String status;         // 状态: draft, pending_approval, approved, rejected, completed, cancelled
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
    
    // 关联字段
    private String clubName;       // 社团名称
    private Integer currentParticipants; // 当前报名人数
}
