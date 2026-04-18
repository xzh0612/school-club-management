package com.club.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {
    private Integer id;
    private String operator;
    private String module;
    private String action;
    private String description;
    private String ip;
    private String userAgent;
    private String status;
    private LocalDateTime createTime;
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
}