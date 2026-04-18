package com.club.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    private Integer applicationId;   // 申请ID
    private Integer userId;          // 用户ID
    private Integer clubId;          // 社团ID
    private Integer recruitmentId;   // 招新ID
    private String introduction;     // 个人简介
    private String status;           // 状态: pending, approved, rejected
    private String comments;         // 审核意见
    private LocalDateTime applyTime;     // 申请时间
    private LocalDateTime reviewTime;    // 审核时间
    
    // 关联字段
    private String userName;         // 申请人姓名
    private String userSid;          // 学号
    private String clubName;         // 社团名称
    private String recruitmentTitle; // 招新标题
}