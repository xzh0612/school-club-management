package com.club.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityChangeRequest {
    private Integer changeId;
    private Integer activityId;
    private Integer requesterId;
    private String title;
    private String content;
    private String type;
    private Integer maxParticipants;
    private LocalDateTime registrationDeadline;
    private String organizer;
    private String contact;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
