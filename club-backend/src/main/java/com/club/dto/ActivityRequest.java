package com.club.dto;

import com.club.entity.Activity;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ActivityRequest(
        @NotNull(message = "活动必须指定所属社团")
        Integer clubId,
        @NotBlank(message = "活动标题不能为空")
        @Size(max = 100, message = "活动标题不能超过100字")
        String title,
        @Size(max = 5000, message = "活动内容不能超过5000字")
        String content,
        String type,
        @NotNull(message = "活动人数上限不能为空")
        @Min(value = 1, message = "活动人数上限必须大于0")
        Integer maxParticipants,
        LocalDateTime registrationDeadline,
        @Size(max = 50, message = "活动负责人不能超过50字")
        String organizer,
        @Size(max = 100, message = "联系方式不能超过100字")
        String contact,
        @NotNull(message = "活动开始时间不能为空")
        @Future(message = "活动开始时间必须晚于当前时间")
        LocalDateTime startTime,
        @NotNull(message = "活动结束时间不能为空")
        LocalDateTime endTime,
        @Size(max = 200, message = "活动地点不能超过200字")
        String location,
        String status
) {
    public Activity toActivity() {
        Activity activity = new Activity();
        activity.setClubId(clubId);
        activity.setTitle(title == null ? null : title.trim());
        activity.setContent(content);
        activity.setType(type);
        activity.setMaxParticipants(maxParticipants);
        activity.setRegistrationDeadline(registrationDeadline);
        activity.setOrganizer(organizer);
        activity.setContact(contact);
        activity.setStartTime(startTime);
        activity.setEndTime(endTime);
        activity.setLocation(location);
        activity.setStatus(status);
        return activity;
    }
}
