package com.club.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupStatusRequest(
        @NotBlank(message = "报名状态不能为空")
        String status
) {
}
