package com.club.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalHistory {
    private Integer historyId;
    private Integer approvalId;
    private Integer stepNo;
    private Integer operatorId;
    private String action;
    private String comments;
    private LocalDateTime createTime;
}
