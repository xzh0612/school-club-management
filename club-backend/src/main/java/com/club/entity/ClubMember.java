package com.club.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubMember {
    private Integer id;
    private Integer clubId;
    private Integer userId;
    private String role;
    private String status;
    private LocalDateTime joinTime;

    private String clubName;
    private String userName;
    private String studentId;
}
