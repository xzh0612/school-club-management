package com.club.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubRanking {
    private Integer rank;           // 排名
    private String name;            // 社团名称
    private Integer events;         // 活动数
    private Integer members;        // 社员数
    private String activity;        // 活跃度百分比
    private String stars;           // 星级评价
}