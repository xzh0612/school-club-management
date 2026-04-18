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
    private LocalDateTime startTime;   // 开始时间
    private LocalDateTime endTime;     // 结束时间
    private String location;       // 活动地点
    private String status;         // 状态: draft, pending_approval, approved, rejected, completed, cancelled
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
    
    // 关联字段
    private String clubName;       // 社团名称
}