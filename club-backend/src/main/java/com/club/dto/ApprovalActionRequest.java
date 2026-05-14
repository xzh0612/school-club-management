package com.club.dto;

import jakarta.validation.constraints.Size;

public record ApprovalActionRequest(
        @Size(max = 1000, message = "审批意见不能超过1000字")
        String comments
) {
    public String normalizedComments() {
        return comments == null ? "" : comments;
    }
}
