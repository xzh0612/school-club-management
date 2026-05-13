package com.club.controller;

import com.club.common.*;
import com.club.entity.*;
import com.club.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ClubService clubService;
    private final ApprovalService approvalService;
    private final SecurityContext securityContext;

    @GetMapping("/activities")
    public Result<PageResult<Activity>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long clubId,
            HttpServletRequest request) {
        if (securityContext.isStudent(request)) {
            return Result.ok(PageResult.of(activityService.list("approved", clubId, page, size), activityService.count("approved", clubId), page, size));
        }
        if (!securityContext.isAdmin(request)) {
            Integer userId = securityContext.currentUserId(request);
            Integer managedClubId = securityContext.managedClubId(request);
            boolean includeAdvisor = securityContext.isTeacher(request);
            return Result.ok(PageResult.of(activityService.listManageable(userId, managedClubId, includeAdvisor, page, size), activityService.countManageable(userId, managedClubId, includeAdvisor), page, size));
        }
        return Result.ok(PageResult.of(activityService.list(status, clubId, page, size), activityService.count(status, clubId), page, size));
    }

    @GetMapping("/activities/{id}")
    public Result<Activity> detail(@PathVariable Integer id, HttpServletRequest request) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (securityContext.isStudent(request) && !"approved".equals(activity.getStatus())) {
            throw new RuntimeException("活动尚未通过审批");
        }
        if (!securityContext.isAdmin(request) && !securityContext.isStudent(request)) {
            securityContext.requireClubManager(request, clubService.getById(activity.getClubId().longValue()));
        }
        return Result.ok(activity);
    }

    @PostMapping("/activities")
    @Transactional
    public Result<Activity> create(@RequestBody Activity activity, HttpServletRequest request) {
        if (activity.getClubId() == null) {
            throw new RuntimeException("活动必须指定所属社团");
        }
        Club club = clubService.getById(activity.getClubId().longValue());
        requireLeaderForClub(request, club);
        activity.setStatus("pending_approval");
        Activity created = activityService.create(activity);

        Approval approval = new Approval();
        approval.setType("activity_application");
        approval.setRelatedId(created.getActivityId());
        approval.setApplicantId(securityContext.currentUserId(request));
        approval.setStatus("pending");
        approval.setComments("申请举办活动：" + created.getTitle());
        approval.setCurrentStep(1);
        approval.setTotalSteps(2);
        approvalService.create(approval);

        return Result.ok(created);
    }

    @PutMapping("/activities/{id}")
    @Transactional
    public Result<Activity> update(@PathVariable Integer id, @RequestBody Activity activity, HttpServletRequest request) {
        Activity existing = activityService.getById(id);
        if (existing == null) {
            throw new RuntimeException("活动不存在");
        }
        requireLeaderForClub(request, clubService.getById(existing.getClubId().longValue()));
        activity.setActivityId(id);
        activity.setClubId(existing.getClubId());
        activity.setStatus("pending_approval");
        Activity updated = activityService.update(activity);

        Approval approval = new Approval();
        approval.setType("activity_application");
        approval.setRelatedId(updated.getActivityId());
        approval.setApplicantId(securityContext.currentUserId(request));
        approval.setStatus("pending");
        approval.setComments("申请变更活动：" + updated.getTitle());
        approval.setCurrentStep(1);
        approval.setTotalSteps(2);
        approvalService.create(approval);

        return Result.ok(updated);
    }

    @DeleteMapping("/activities/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        Activity existing = activityService.getById(id);
        if (existing == null) {
            throw new RuntimeException("活动不存在");
        }
        requireLeaderForClub(request, clubService.getById(existing.getClubId().longValue()));
        activityService.delete(id);
        return Result.ok();
    }

    @GetMapping("/activities/search")
    public Result<PageResult<Activity>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        if (securityContext.isStudent(request)) {
            return Result.ok(PageResult.of(activityService.searchByStatus(keyword, "approved", page, size), activityService.searchCountByStatus(keyword, "approved"), page, size));
        }
        if (!securityContext.isAdmin(request)) {
            Integer userId = securityContext.currentUserId(request);
            Integer clubId = securityContext.managedClubId(request);
            boolean includeAdvisor = securityContext.isTeacher(request);
            return Result.ok(PageResult.of(activityService.searchManageable(keyword, userId, clubId, includeAdvisor, page, size), activityService.searchManageableCount(keyword, userId, clubId, includeAdvisor), page, size));
        }
        return Result.ok(PageResult.of(activityService.search(keyword, page, size), activityService.searchCount(keyword), page, size));
    }

    @PostMapping("/activities/{id}/signup")
    public Result<Void> signup(@PathVariable Integer id, HttpServletRequest request) {
        if (!securityContext.isStudent(request)) {
            throw new RuntimeException("只有学生可以报名活动");
        }
        activityService.signup(id, securityContext.currentUserId(request));
        return Result.ok();
    }

    @DeleteMapping("/activities/{id}/signup")
    public Result<Void> cancelSignup(@PathVariable Integer id, @RequestParam(required = false) Integer userId, HttpServletRequest request) {
        Integer currentUserId = securityContext.currentUserId(request);
        if (userId == null && !securityContext.isStudent(request)) {
            throw new RuntimeException("只有学生可以取消自己的报名");
        }
        if (userId != null && !securityContext.isAdmin(request)) {
            Activity activity = activityService.getById(id);
            if (activity == null) {
                throw new RuntimeException("活动不存在");
            }
            securityContext.requireClubManager(request, clubService.getById(activity.getClubId().longValue()));
        }
        activityService.cancelSignup(id, userId != null ? userId : currentUserId);
        return Result.ok();
    }

    @GetMapping("/activities/{id}/signups")
    public Result<PageResult<ActivitySignup>> signups(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        requireLeaderForClub(request, clubService.getById(activity.getClubId().longValue()));
        return Result.ok(PageResult.of(activityService.getSignups(id, page, size), activityService.getSignupCount(id), page, size));
    }

    private void requireLeaderForClub(HttpServletRequest request, Club club) {
        if (!securityContext.isLeader(request)) {
            throw new RuntimeException("活动由社团负责人提交和管理，管理员或老师只负责审批");
        }
        securityContext.requireClubManager(request, club);
    }
}
