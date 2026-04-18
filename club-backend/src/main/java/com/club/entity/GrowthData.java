package com.club.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrowthData {
    private String year;            // 年份
    private Integer count;          // 社团数量
    private String percentage;      // 百分比显示
    private Boolean current;        // 是否为当前年份
}