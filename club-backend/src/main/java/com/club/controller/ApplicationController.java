package com.club.controller;

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
        if (!securityContext.isAdmin(request) && !securityContext.isTeacher(request)) {
            Integer managedClubId = securityContext.currentUser(request).getClubId();
            return Result.ok(PageResult.of(
                    applicationService.list(managedClubId, status, securityContext.currentUserId(request), false, page, size),
                    applicationService.count(managedClubId, status, securityContext.currentUserId(request), false),
                    page,
                    size));
        }
        return Result.ok(PageResult.of(
                applicationService.list(clubId, status, securityContext.currentUserId(request), true, page, size),
                applicationService.count(clubId, status, securityContext.currentUserId(request), true),
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
        if (!securityContext.isAdmin(request) && !securityContext.isTeacher(request) && !securityContext.isStudent(request)) {
            Club club = clubService.getById(application.getClubId().longValue());
            securityContext.requireClubManager(request, club);
        }
        return Result.ok(application);
    }

    @PostMapping("/applications")
    public Result<Application> submit(
            @RequestParam Integer clubId,
            @RequestParam(required = false) Integer recruitmentId,
            @RequestBody(required = false) String introduction,
            HttpServletRequest request) {
        if (!securityContext.isStudent(request)) {
            throw new RuntimeException("只有学生可以提交入社申请");
        }
        return Result.ok(applicationService.submit(securityContext.currentUserId(request), clubId, recruitmentId, introduction));
    }

    @PostMapping("/applications/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id, @RequestBody(required = false) java.util.Map<String, Object> body, HttpServletRequest request) {
        Application application = applicationService.getById(id);
        if (application == null) {
            throw new RuntimeException("入社申请不存在");
        }
        securityContext.requireClubManager(request, clubService.getById(application.getClubId().longValue()));
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
        securityContext.requireClubManager(request, clubService.getById(application.getClubId().longValue()));
        String comments = body != null ? (String) body.get("comments") : "";
        applicationService.reject(id, securityContext.currentUserId(request), comments != null ? comments : "");
        return Result.ok();
    }
}
