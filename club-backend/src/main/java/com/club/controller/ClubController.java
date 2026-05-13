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
public class ClubController {

    private final ClubService clubService;
    private final UserService userService;
    private final ClubMemberService clubMemberService;
    private final ApprovalService approvalService;
    private final SecurityContext securityContext;

    @GetMapping("/clubs")
    public Result<PageResult<Club>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        if (securityContext.isStudent(request)) {
            return Result.ok(PageResult.of(clubService.list("approved", keyword, page, size), clubService.count("approved", keyword), page, size));
        }
        if (!securityContext.isAdmin(request)) {
            Integer userId = securityContext.currentUserId(request);
            Integer clubId = securityContext.currentUser(request).getClubId();
            return Result.ok(PageResult.of(clubService.listManageable(userId, clubId, page, size), clubService.countManageable(userId, clubId), page, size));
        }
        return Result.ok(PageResult.of(clubService.list(status, keyword, page, size), clubService.count(status, keyword), page, size));
    }

    @GetMapping("/clubs/{id}")
    public Result<Club> detail(@PathVariable Long id, HttpServletRequest request) {
        Club club = clubService.getById(id);
        if (securityContext.isStudent(request) && !"approved".equals(club.getStatus())) {
            throw new RuntimeException("社团尚未通过审批");
        }
        if (!securityContext.isAdmin(request) && !securityContext.isStudent(request)) {
            securityContext.requireClubManager(request, club);
        }
        return Result.ok(club);
    }

    @PostMapping("/clubs")
    public Result<Club> create(@RequestBody Club club, HttpServletRequest request) {
        Integer applicantId = securityContext.currentUserId(request);
        if (securityContext.isAdmin(request)) {
            if (club.getStatus() == null || club.getStatus().isBlank()) {
                club.setStatus("approved");
            }
        } else {
            club.setFounderId(applicantId);
            club.setStatus("pending");
        }
        Club created = clubService.create(club);
        if (!securityContext.isAdmin(request)) {
            Approval approval = new Approval();
            approval.setType("club_creation");
            approval.setRelatedId(created.getClubId());
            approval.setApplicantId(applicantId);
            approval.setStatus("pending");
            approval.setComments("申请创建社团：" + created.getClubName());
            approval.setCurrentStep(1);
            approval.setTotalSteps(2);
            approvalService.create(approval);
        }
        return Result.ok(created);
    }

    @PutMapping("/clubs/{id}")
    public Result<Club> update(@PathVariable Integer id, @RequestBody Club club, HttpServletRequest request) {
        securityContext.requireClubManager(request, clubService.getById(id.longValue()));
        club.setClubId(id);
        return Result.ok(clubService.update(club));
    }

    @DeleteMapping("/clubs/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        clubService.delete((long)id);
        return Result.ok();
    }

    @GetMapping("/clubs/search")
    public Result<PageResult<Club>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        if (securityContext.isStudent(request)) {
            return Result.ok(PageResult.of(clubService.searchByStatus(keyword, "approved", page, size), clubService.searchCountByStatus(keyword, "approved"), page, size));
        }
        if (!securityContext.isAdmin(request)) {
            Integer userId = securityContext.currentUserId(request);
            Integer clubId = securityContext.currentUser(request).getClubId();
            return Result.ok(PageResult.of(clubService.searchManageable(keyword, userId, clubId, page, size), clubService.searchManageableCount(keyword, userId, clubId), page, size));
        }
        return Result.ok(PageResult.of(clubService.search(keyword, page, size), clubService.searchCount(keyword), page, size));
    }

    @GetMapping("/clubs/{id}/members")
    public Result<PageResult<ClubMember>> members(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        Club club = clubService.getById(id.longValue());
        if (securityContext.isStudent(request) && !"approved".equals(club.getStatus())) {
            throw new RuntimeException("社团尚未通过审批");
        }
        if (!securityContext.isStudent(request)) {
            securityContext.requireClubManager(request, club);
        }
        return Result.ok(PageResult.of(clubMemberService.listByClub(id, page, size), clubMemberService.countByClub(id), page, size));
    }

    @PostMapping("/clubs/{id}/members")
    public Result<ClubMember> addMember(
            @PathVariable Integer id,
            @RequestBody java.util.Map<String, Object> body,
            HttpServletRequest request) {
        securityContext.requireClubLeader(request, clubService.getById(id.longValue()));
        Integer userId = ((Number) body.get("userId")).intValue();
        String role = (String) body.get("role");
        return Result.ok(clubMemberService.addMember(id, userId, role));
    }

    @PutMapping("/clubs/{id}/members/{userId}")
    public Result<ClubMember> updateMember(
            @PathVariable Integer id,
            @PathVariable Integer userId,
            @RequestBody java.util.Map<String, Object> body,
            HttpServletRequest request) {
        securityContext.requireClubLeader(request, clubService.getById(id.longValue()));
        String role = (String) body.get("role");
        String status = (String) body.get("status");
        return Result.ok(clubMemberService.updateMember(id, userId, role, status));
    }

    @DeleteMapping("/clubs/{id}/members/{userId}")
    public Result<Void> removeMember(
            @PathVariable Integer id,
            @PathVariable Integer userId,
            HttpServletRequest request) {
        securityContext.requireClubLeader(request, clubService.getById(id.longValue()));
        clubMemberService.removeMember(id, userId);
        return Result.ok();
    }

    @GetMapping("/clubs/{id}/activities")
    public Result<PageResult<Activity>> activities(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        Club club = clubService.getById(id.longValue());
        if (securityContext.isStudent(request) && !"approved".equals(club.getStatus())) {
            throw new RuntimeException("社团尚未通过审批");
        }
        if (!securityContext.isStudent(request)) {
            securityContext.requireClubManager(request, club);
        }
        String status = securityContext.isStudent(request) ? "approved" : null;
        return Result.ok(PageResult.of(clubService.getActivities(id.longValue(), status, page, size), clubService.getActivityCount(id.longValue(), status), page, size));
    }
}
