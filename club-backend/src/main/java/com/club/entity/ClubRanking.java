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
}
