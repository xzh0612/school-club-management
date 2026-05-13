package com.club.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentPlan {
    private Integer recruitmentId;
    private Integer clubId;
    private String title;
    private Integer quota;
    private String requirements;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String clubName;
    private Integer appliedCount;
}
