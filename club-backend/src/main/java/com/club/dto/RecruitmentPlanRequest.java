package com.club.dto;

import com.club.entity.RecruitmentPlan;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RecruitmentPlanRequest(
        @NotNull(message = "招新计划必须指定所属社团")
        Integer clubId,
        @NotBlank(message = "招新标题不能为空")
        @Size(max = 100, message = "招新标题不能超过100字")
        String title,
        @NotNull(message = "招新名额不能为空")
        @Min(value = 1, message = "招新名额必须大于0")
        Integer quota,
        @Size(max = 2000, message = "招新要求不能超过2000字")
        String requirements,
        @Size(max = 5000, message = "招新说明不能超过5000字")
        String description,
        String status,
        @NotNull(message = "招新开始日期不能为空")
        @FutureOrPresent(message = "招新开始日期不能早于今天")
        LocalDate startDate,
        @NotNull(message = "招新结束日期不能为空")
        LocalDate endDate
) {
    public RecruitmentPlan toPlan() {
        RecruitmentPlan plan = new RecruitmentPlan();
        plan.setClubId(clubId);
        plan.setTitle(title == null ? null : title.trim());
        plan.setQuota(quota);
        plan.setRequirements(requirements);
        plan.setDescription(description);
        plan.setStatus(status);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        return plan;
    }
}
