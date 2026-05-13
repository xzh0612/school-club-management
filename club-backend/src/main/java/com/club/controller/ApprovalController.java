package com.club.controller;

import com.club.common.*;
import com.club.entity.*;
import com.club.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;
    private final SecurityContext securityContext;

    @GetMapping("/approvals")
    public Result<PageResult<Approval>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        if ("recruitment_application".equals(type)) {
            throw new RuntimeException("入社申请由社团负责人在招新管理中审核");
        }
        return Result.ok(PageResult.of(approvalService.list(type, status, page, size), approvalService.count(type, status), page, size));
    }

    @GetMapping("/approvals/{id}")
    public Result<Approval> detail(@PathVariable Integer id, HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        return Result.ok(approvalService.getById(id));
    }

    @GetMapping("/approvals/pending")
    public Result<PageResult<Approval>> pendingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        return Result.ok(PageResult.of(approvalService.pendingList(page, size), approvalService.pendingCount(), page, size));
    }

    @GetMapping("/approvals/pending/count")
    public Result<Integer> pendingCount(HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        return Result.ok(approvalService.pendingCount());
    }

    @PostMapping("/approvals")
    public Result<Approval> create(@RequestBody Approval approval, HttpServletRequest request) {
        throw new RuntimeException("审批记录由社团成立或活动申请流程自动生成，不能手工创建");
    }

    @PutMapping("/approvals/{id}")
    public Result<Approval> update(@PathVariable Integer id, @RequestBody Approval approval, HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        approval.setApprovalId(id);
        return Result.ok(approvalService.update(approval));
    }

    @DeleteMapping("/approvals/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        approvalService.delete(id);
        return Result.ok();
    }

    @PostMapping("/approvals/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id, @RequestBody(required = false) java.util.Map<String, Object> requestBody, HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        String comments = requestBody != null ? (String) requestBody.get("comments") : "";
        approvalService.approve(id, securityContext.currentUserId(request), comments != null ? comments : "");
        return Result.ok();
    }

    @PostMapping(value = "/approvals/{id}/reject", consumes = "application/json")
    public Result<Void> reject(@PathVariable Integer id, @RequestBody(required = false) java.util.Map<String, Object> requestBody, HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
        String comments = requestBody != null ? (String) requestBody.get("comments") : "";
        approvalService.reject(id, securityContext.currentUserId(request), comments != null ? comments : "");
        return Result.ok();
    }

    @GetMapping("/approvals/search")
    public Result<PageResult<Approval>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        securityContext.requireAdminOrTeacher(request);
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
}
