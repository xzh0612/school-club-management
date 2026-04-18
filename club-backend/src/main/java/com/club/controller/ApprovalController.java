package com.club.controller;

import com.club.common.*;
import com.club.entity.*;
import com.club.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping("/approvals")
    public Result<PageResult<Approval>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        return Result.ok(PageResult.of(approvalService.list(type, status, page, size), approvalService.count(type, status), page, size));
    }

    @GetMapping("/approvals/{id}")
    public Result<Approval> detail(@PathVariable Integer id) {
        return Result.ok(approvalService.getById(id));
    }

    @GetMapping("/approvals/pending")
    public Result<PageResult<Approval>> pendingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(PageResult.of(approvalService.pendingList(page, size), approvalService.pendingCount(), page, size));
    }

    @GetMapping("/approvals/pending/count")
    public Result<Integer> pendingCount() {
        return Result.ok(approvalService.pendingCount());
    }

    @PostMapping("/approvals")
    public Result<Approval> create(@RequestBody Approval approval) {
        return Result.ok(approvalService.create(approval));
    }

    @PutMapping("/approvals/{id}")
    public Result<Approval> update(@PathVariable Integer id, @RequestBody Approval approval) {
        approval.setApprovalId(id);
        return Result.ok(approvalService.update(approval));
    }

    @DeleteMapping("/approvals/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        approvalService.delete(id);
        return Result.ok();
    }

    @PostMapping("/approvals/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id, @RequestBody(required = false) java.util.Map<String, Object> requestBody) {
        System.out.println("=== 收到审批通过请求 ===");
        System.out.println("审批ID: " + id);
        System.out.println("请求体: " + requestBody);
        System.out.println("请求体类型: " + (requestBody != null ? requestBody.getClass().getName() : "null"));
        
        try {
            String comments = requestBody != null ? (String) requestBody.get("comments") : "";
            System.out.println("审批意见: " + comments);
            
            approvalService.approve(id, comments != null ? comments : "");
            System.out.println("审批通过成功");
            return Result.ok();
        } catch (Exception e) {
            System.err.println("审批通过时发生错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error("审批失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/approvals/{id}/reject", consumes = "application/json")
    public Result<Void> reject(@PathVariable Integer id, @RequestBody(required = false) java.util.Map<String, Object> requestBody) {
        System.out.println("=== 收到审批驳回请求 ===");
        System.out.println("审批ID: " + id);
        System.out.println("请求体: " + requestBody);
        System.out.println("请求体类型: " + (requestBody != null ? requestBody.getClass().getName() : "null"));
        
        try {
            String comments = requestBody != null ? (String) requestBody.get("comments") : "";
            System.out.println("审批意见: " + comments);
            
            approvalService.reject(id, comments != null ? comments : "");
            System.out.println("审批驳回成功");
            return Result.ok();
        } catch (Exception e) {
            System.err.println("审批驳回时发生错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error("审批失败: " + e.getMessage());
        }
    }

    @GetMapping("/approvals/search")
    public Result<PageResult<Approval>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(PageResult.of(approvalService.search(keyword, page, size), approvalService.searchCount(keyword), page, size));
    }

    // 获取我的审批列表
    @GetMapping("/approvals/my")
    public Result<PageResult<Approval>> myApprovals(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(PageResult.of(approvalService.getByApprover(userId, page, size), approvalService.countByApprover(userId), page, size));
    }

    // 获取我提交的审批列表
    @GetMapping("/approvals/submitted")
    public Result<PageResult<Approval>> submittedApprovals(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(PageResult.of(approvalService.getByApplicant(userId, page, size), approvalService.countByApplicant(userId), page, size));
    }
    
    // 提交招新申请
    @PostMapping("/recruitment-applications")
    public Result<Approval> submitRecruitmentApplication(
            @RequestParam Integer userId,
            @RequestParam Integer clubId,
            @RequestBody String introduction) {
        return Result.ok(approvalService.submitRecruitmentApplication(userId, clubId, introduction));
    }
    
    // 获取待审批的招新申请数量
    @GetMapping("/recruitment-applications/pending-count")
    public Result<Integer> getPendingRecruitmentCount() {
        return Result.ok(approvalService.countPendingRecruitmentApplications());
    }
}