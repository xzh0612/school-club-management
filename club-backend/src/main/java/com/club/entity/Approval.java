package com.club.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Approval {
    private Integer approvalId;      // 审批ID
    private String type;             // 审批类型: club_creation, activity_application
    private Integer relatedId;       // 关联ID
    private Integer applicantId;     // 申请人ID
    private Integer approverId;      // 审批人ID
    private String status;           // 状态: pending, approved, rejected
    private String comments;         // 审批意见
    private Integer currentStep;     // 当前步骤
    private Integer totalSteps;      // 总步骤数
    private LocalDateTime createTime;    // 申请时间
    private LocalDateTime updateTime;    // 更新时间
    
    // 关联字段
    private String applicantName;    // 申请人姓名
    private String approverName;     // 审批人姓名
    private String relatedTitle;     // 关联标题（社团名或活动名）
}