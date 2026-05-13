package com.club.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    private Integer clubId;        // 社团ID
    private String clubName;       // 社团名称
    private String description;    // 社团简介
    private String clubType;       // 社团类型
    private Integer founderId;     // 创始人ID
    private Integer advisorId;     // 指导老师ID
    private String status;         // 状态: pending, approved, rejected, inactive
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
    
    // 关联字段（非数据库字段）
    private String founderName;    // 创始人姓名
    private String advisorName;    // 指导老师姓名
}
