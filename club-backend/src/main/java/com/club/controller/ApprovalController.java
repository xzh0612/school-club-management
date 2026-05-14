package com.club.controller;

import com.club.common.*;
import com.club.dto.ApprovalActionRequest;
import com.club.entity.*;
import com.club.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;
    private final SecurityContext securityContext;
    private final ClubService clubService;
    private final ActivityService activityService;

    @GetMapping("/approvals")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<PageResult<Approval>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        if (securityContext.isTeacher(request)) {
            Integer advisorId = securityContext.currentUserId(request);
            return Result.ok(PageResult.of(
                    approvalService.listForAdvisor(advisorId, type, status, page, size),
                    approvalService.countForAdvisor(advisorId, type, status),
                    page,
                    size));
        }
        return Result.ok(PageResult.of(approvalService.list(type, status, page, size), approvalService.count(type, status), page, size));
    }

    @GetMapping("/approvals/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<Approval> detail(@PathVariable Integer id, HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        Approval approval = approvalService.getById(id);
        requireApprovalAccess(request, approval);
        return Result.ok(approval);
    }

    @GetMapping("/approvals/{id}/histories")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<java.util.List<ApprovalHistory>> histories(@PathVariable Integer id, HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        Approval approval = approvalService.getById(id);
        requireApprovalAccess(request, approval);
        return Result.ok(approvalService.histories(id));
    }

    @GetMapping("/approvals/pending")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<PageResult<Approval>> pendingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        if (securityContext.isTeacher(request)) {
            Integer advisorId = securityContext.currentUserId(request);
            return Result.ok(PageResult.of(
                    approvalService.pendingListForAdvisor(advisorId, page, size),
                    approvalService.pendingCountForAdvisor(advisorId),
                    page,
                    size));
        }
        return Result.ok(PageResult.of(approvalService.pendingList(page, size), approvalService.pendingCount(), page, size));
    }

    @GetMapping("/approvals/pending/count")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<Integer> pendingCount(HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        if (securityContext.isTeacher(request)) {
            return Result.ok(approvalService.pendingCountForAdvisor(securityContext.currentUserId(request)));
        }
        return Result.ok(approvalService.pendingCount());
    }

    @PostMapping("/approvals")
    public Result<Approval> create(HttpServletRequest request) {
        throw new RuntimeException("审批记录由社团成立或活动申请流程自动生成，不能手工创建");
    }

    @PutMapping("/approvals/{id}")
    public Result<Approval> update(@PathVariable Integer id, HttpServletRequest request) {
        throw new RuntimeException("审批记录不能手工修改，请使用通过或驳回操作以同步业务状态");
    }

    @DeleteMapping("/approvals/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        approvalService.delete(id);
        return Result.ok();
    }

    @PostMapping("/approvals/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> archive(@PathVariable Integer id, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        approvalService.delete(id);
        return Result.ok();
    }

    @PostMapping("/approvals/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<Void> approve(@PathVariable Integer id, @Valid @RequestBody(required = false) ApprovalActionRequest requestBody, HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        Approval approval = approvalService.getById(id);
        requireApprovalAccess(request, approval);
        requireApprovalStepPermission(request, approval);
        approvalService.approve(id, securityContext.currentUserId(request), requestBody != null ? requestBody.normalizedComments() : "");
        return Result.ok();
    }

    @PostMapping("/approvals/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<Void> reject(@PathVariable Integer id, @Valid @RequestBody(required = false) ApprovalActionRequest requestBody, HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        Approval approval = approvalService.getById(id);
        requireApprovalAccess(request, approval);
        requireApprovalStepPermission(request, approval);
        approvalService.reject(id, securityContext.currentUserId(request), requestBody != null ? requestBody.normalizedComments() : "");
        return Result.ok();
    }

    @GetMapping("/approvals/search")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<PageResult<Approval>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        if (securityContext.isTeacher(request)) {
            Integer advisorId = securityContext.currentUserId(request);
            return Result.ok(PageResult.of(
                    approvalService.searchForAdvisor(advisorId, keyword, page, size),
                    approvalService.searchCountForAdvisor(advisorId, keyword),
                    page,
                    size));
        }
        return Result.ok(PageResult.of(approvalService.search(keyword, page, size), approvalService.searchCount(keyword), page, size));
    }

    // 获取我的审批列表
    @GetMapping("/approvals/my")
    public Result<PageResult<Approval>> myApprovals(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        Integer userId = securityContext.currentUserId(request);
        return Result.ok(PageResult.of(approvalService.getByApprover(userId, page, size), approvalService.countByApprover(userId), page, size));
    }

    // 获取我提交的审批列表
    @GetMapping("/approvals/submitted")
    public Result<PageResult<Approval>> submittedApprovals(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        Integer userId = securityContext.currentUserId(request);
        return Result.ok(PageResult.of(approvalService.getByApplicant(userId, page, size), approvalService.countByApplicant(userId), page, size));
    }

    private void requireApprovalAccess(HttpServletRequest request, Approval approval) {
        if (approval == null) {
            throw new RuntimeException("审批记录不存在");
        }
        if (securityContext.isAdmin(request)) {
            return;
        }
        securityContext.requireAdminOrTeacher(request);
        if (!securityContext.isTeacher(request)) {
            return;
        }
        Integer advisorId = securityContext.currentUserId(request);
        if (!canAdvisorAccessApproval(advisorId, approval)) {
            throw new RuntimeException("只能查看或处理自己指导社团的审批");
        }
    }

    private boolean canAdvisorAccessApproval(Integer advisorId, Approval approval) {
        if (advisorId == null || approval.getRelatedId() == null) {
            return false;
        }
        return switch (approval.getType()) {
            case "club_creation" -> {
                Club club = clubService.getById(approval.getRelatedId().longValue());
                yield club != null && advisorId.equals(club.getAdvisorId());
            }
            case "activity_application" -> {
                Activity activity = activityService.getById(approval.getRelatedId());
                if (activity == null || activity.getClubId() == null) {
                    yield false;
                }
                Club club = clubService.getById(activity.getClubId().longValue());
                yield club != null && advisorId.equals(club.getAdvisorId());
            }
            default -> false;
        };
    }

    private void requireApprovalStepPermission(HttpServletRequest request, Approval approval) {
        int currentStep = approval.getCurrentStep() == null ? 1 : approval.getCurrentStep();
        int totalSteps = approval.getTotalSteps() == null ? 1 : approval.getTotalSteps();
        if (securityContext.isTeacher(request)) {
            if (totalSteps <= 1 || currentStep != 1) {
                throw new RuntimeException("指导老师只能处理第一步审批");
            }
            return;
        }
        if (securityContext.isAdmin(request) && totalSteps > 1 && currentStep < totalSteps) {
            throw new RuntimeException("前置审批未完成，管理员暂不能处理");
        }
    }
}
