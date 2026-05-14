package com.club.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationRequest(
        @NotNull(message = "入社申请必须指定社团")
        Integer clubId,
        Integer recruitmentId,
        @Size(max = 1000, message = "申请理由不能超过1000字")
        String introduction
) {
}
