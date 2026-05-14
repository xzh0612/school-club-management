package com.club.controller;

import com.club.common.PageResult;
import com.club.common.Result;
import com.club.common.SecurityContext;
import com.club.dto.ApplicationRequest;
import com.club.dto.ApprovalActionRequest;
import com.club.entity.Application;
import com.club.entity.Club;
import com.club.service.ApplicationService;
import com.club.service.ClubService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ClubService clubService;
    private final SecurityContext securityContext;

    @GetMapping("/applications")
    public Result<PageResult<Application>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer clubId,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        if (securityContext.isStudent(request)) {
            Integer userId = securityContext.currentUserId(request);
            return Result.ok(PageResult.of(applicationService.listByUser(userId, page, size), applicationService.countByUser(userId), page, size));
        }
        if (!securityContext.isLeader(request)) {
            throw new RuntimeException("只有社团负责人可以查看入社申请");
        }
        Integer managedClubId = clubId != null ? clubId : securityContext.requireLeaderClubId(request);
        securityContext.requireClubLeader(request, clubService.getById(managedClubId.longValue()));
        return Result.ok(PageResult.of(
                applicationService.list(managedClubId, status, securityContext.currentUserId(request), false, page, size),
                applicationService.count(managedClubId, status, securityContext.currentUserId(request), false),
                page,
                size));
    }

    @GetMapping("/applications/{id}")
    public Result<Application> detail(@PathVariable Integer id, HttpServletRequest request) {
        Application application = applicationService.getById(id);
        if (application == null) {
            throw new RuntimeException("入社申请不存在");
        }
        if (securityContext.isStudent(request) && !securityContext.currentUserId(request).equals(application.getUserId())) {
            throw new RuntimeException("只能查看自己的入社申请");
        }
        if (!securityContext.isStudent(request)) {
            Club club = clubService.getById(application.getClubId().longValue());
            securityContext.requireClubLeader(request, club);
        }
        return Result.ok(application);
    }

    @PostMapping("/applications")
    public Result<Application> submit(
            @Valid @RequestBody ApplicationRequest requestBody,
            HttpServletRequest request) {
        if (!securityContext.isStudent(request)) {
            throw new RuntimeException("只有学生可以提交入社申请");
        }
        return Result.ok(applicationService.submit(
                securityContext.currentUserId(request),
                requestBody.clubId(),
                requestBody.recruitmentId(),
                requestBody.introduction()));
    }

    @PostMapping("/applications/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id, @Valid @RequestBody(required = false) ApprovalActionRequest body, HttpServletRequest request) {
        Application application = applicationService.getById(id);
        if (application == null) {
            throw new RuntimeException("入社申请不存在");
        }
        securityContext.requireClubLeader(request, clubService.getById(application.getClubId().longValue()));
        applicationService.approve(id, securityContext.currentUserId(request), body != null ? body.normalizedComments() : "");
        return Result.ok();
    }

    @PostMapping("/applications/{id}/reject")
    public Result<Void> reject(@PathVariable Integer id, @Valid @RequestBody(required = false) ApprovalActionRequest body, HttpServletRequest request) {
        Application application = applicationService.getById(id);
        if (application == null) {
            throw new RuntimeException("入社申请不存在");
        }
        securityContext.requireClubLeader(request, clubService.getById(application.getClubId().longValue()));
        applicationService.reject(id, securityContext.currentUserId(request), body != null ? body.normalizedComments() : "");
        return Result.ok();
    }
}
