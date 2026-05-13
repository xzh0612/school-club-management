package com.club.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.club.common.PageResult;
import com.club.common.Result;
import com.club.common.SecurityContext;
import com.club.entity.Application;
import com.club.entity.Club;
import com.club.service.ApplicationService;
import com.club.service.ClubService;
import jakarta.servlet.http.HttpServletRequest;
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
            @RequestParam(required = false) Integer clubId,
            @RequestParam(required = false) Integer recruitmentId,
            @RequestBody(required = false) JsonNode body,
            HttpServletRequest request) {
        if (!securityContext.isStudent(request)) {
            throw new RuntimeException("只有学生可以提交入社申请");
        }
        Integer effectiveClubId = body != null && body.hasNonNull("clubId") ? body.get("clubId").asInt() : clubId;
        Integer effectiveRecruitmentId = body != null && body.hasNonNull("recruitmentId") ? body.get("recruitmentId").asInt() : recruitmentId;
        String introduction = null;
        if (body != null) {
            if (body.isTextual()) {
                introduction = body.asText();
            } else if (body.hasNonNull("introduction")) {
                introduction = body.get("introduction").asText();
            }
        }
        if (effectiveClubId == null) {
            throw new RuntimeException("入社申请必须指定社团");
        }
        return Result.ok(applicationService.submit(securityContext.currentUserId(request), effectiveClubId, effectiveRecruitmentId, introduction));
    }

    @PostMapping("/applications/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id, @RequestBody(required = false) java.util.Map<String, Object> body, HttpServletRequest request) {
        Application application = applicationService.getById(id);
        if (application == null) {
            throw new RuntimeException("入社申请不存在");
        }
        securityContext.requireClubLeader(request, clubService.getById(application.getClubId().longValue()));
        String comments = body != null ? (String) body.get("comments") : "";
        applicationService.approve(id, securityContext.currentUserId(request), comments != null ? comments : "");
        return Result.ok();
    }

    @PostMapping("/applications/{id}/reject")
    public Result<Void> reject(@PathVariable Integer id, @RequestBody(required = false) java.util.Map<String, Object> body, HttpServletRequest request) {
        Application application = applicationService.getById(id);
        if (application == null) {
            throw new RuntimeException("入社申请不存在");
        }
        securityContext.requireClubLeader(request, clubService.getById(application.getClubId().longValue()));
        String comments = body != null ? (String) body.get("comments") : "";
        applicationService.reject(id, securityContext.currentUserId(request), comments != null ? comments : "");
        return Result.ok();
    }
}
