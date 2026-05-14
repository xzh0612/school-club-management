package com.club.dto;

import com.club.entity.Announcement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnnouncementRequest(
        @NotBlank(message = "公告标题不能为空")
        @Size(max = 200, message = "公告标题不能超过200字")
        String title,
        @NotBlank(message = "公告内容不能为空")
        @Size(max = 10000, message = "公告内容不能超过10000字")
        String content,
        String targetType,
        Integer targetId,
        String status,
        Integer isTop
) {
    public Announcement toAnnouncement() {
        Announcement announcement = new Announcement();
        announcement.setTitle(title == null ? null : title.trim());
        announcement.setContent(content);
        announcement.setTargetType(targetType);
        announcement.setTargetId(targetId);
        announcement.setStatus(status);
        announcement.setIsTop(isTop);
        return announcement;
    }
}
