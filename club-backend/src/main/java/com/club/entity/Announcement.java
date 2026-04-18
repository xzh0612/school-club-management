package com.club.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    private Integer announcementId;  // 公告ID
    private String title;            // 公告标题
    private String content;          // 公告内容
    private Integer publisherId;     // 发布人ID
    private String targetType;       // 发布范围: all, club
    private Integer targetId;        // 目标ID
    private String status;           // 状态: draft, published, revoked
    private Integer isTop;           // 是否置顶: 0否, 1是
    private Integer viewCount;       // 浏览次数
    private LocalDateTime publishTime;   // 发布时间
    private LocalDateTime updateTime;    // 更新时间
    
    // 关联字段
    private String publisherName;    // 发布人姓名
}