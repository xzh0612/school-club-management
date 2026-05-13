package com.club.controller;

import com.club.common.PageResult;
import com.club.common.Result;
import com.club.common.SecurityContext;
import com.club.entity.Club;
import com.club.entity.RecruitmentPlan;
import com.club.service.ClubService;
import com.club.service.RecruitmentPlanService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruitment-plans")
@RequiredArgsConstructor
public class RecruitmentPlanController {

    private final RecruitmentPlanService recruitmentPlanService;
    private final ClubService clubService;
    private final SecurityContext securityContext;

    @GetMapping
    public Result<PageResult<RecruitmentPlan>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer clubId,
            HttpServletRequest request) {
        Integer effectiveClubId = clubId;
        if (!securityContext.isAdmin(request) && !securityContext.isTeacher(request) && !securityContext.isStudent(request)) {
            effectiveClubId = securityContext.currentUser(request).getClubId();
        }
        return Result.ok(PageResult.of(
                recruitmentPlanService.list(effectiveClubId, page, size),
                recruitmentPlanService.count(effectiveClubId),
                page,
                size));
    }

    @PostMapping
    public Result<RecruitmentPlan> create(@RequestBody RecruitmentPlan plan, HttpServletRequest request) {
        Club club = clubService.getById(plan.getClubId().longValue());
        securityContext.requireClubManager(request, club);
        return Result.ok(recruitmentPlanService.create(plan));
    }

    @PutMapping("/{id}")
    public Result<RecruitmentPlan> update(@PathVariable Integer id, @RequestBody RecruitmentPlan plan, HttpServletRequest request) {
        RecruitmentPlan existing = recruitmentPlanService.getById(id);
        if (existing == null) {
            throw new RuntimeException("招新计划不存在");
        }
        securityContext.requireClubManager(request, clubService.getById(existing.getClubId().longValue()));
        plan.setRecruitmentId(id);
        if (plan.getClubId() == null) {
            plan.setClubId(existing.getClubId());
        }
        return Result.ok(recruitmentPlanService.update(plan));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        RecruitmentPlan existing = recruitmentPlanService.getById(id);
        if (existing == null) {
            throw new RuntimeException("招新计划不存在");
        }
        securityContext.requireClubManager(request, clubService.getById(existing.getClubId().longValue()));
        recruitmentPlanService.delete(id);
        return Result.ok();
    }
}
