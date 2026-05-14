package com.club.controller;

import com.club.common.*;
import com.club.dto.ClubMemberUpdateRequest;
import com.club.dto.ClubRequest;
import com.club.entity.*;
import com.club.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
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
            @RequestParam(required = false) String clubType,
            HttpServletRequest request) {
        if (securityContext.isStudent(request)) {
            return Result.ok(PageResult.of(clubService.list("approved", keyword, clubType, page, size), clubService.count("approved", keyword, clubType), page, size));
        }
        if (!securityContext.isAdmin(request)) {
            Integer userId = securityContext.currentUserId(request);
            Integer clubId = securityContext.managedClubId(request);
            boolean includeAdvisor = securityContext.isTeacher(request);
            return Result.ok(PageResult.of(clubService.listManageable(userId, clubId, includeAdvisor, page, size), clubService.countManageable(userId, clubId, includeAdvisor), page, size));
        }
        return Result.ok(PageResult.of(clubService.list(status, keyword, clubType, page, size), clubService.count(status, keyword, clubType), page, size));
    }

    @GetMapping("/clubs/stats")
    public Result<java.util.Map<String, Integer>> stats(HttpServletRequest request) {
        if (securityContext.isStudent(request)) {
            throw new RuntimeException("学生无权查看社团管理统计");
        }
        if (securityContext.isAdmin(request)) {
            return Result.ok(clubService.stats(null, null, false, true));
        }
        Integer userId = securityContext.currentUserId(request);
        Integer clubId = securityContext.managedClubId(request);
        return Result.ok(clubService.stats(userId, clubId, securityContext.isTeacher(request), false));
    }

    @GetMapping("/clubs/{id}")
    public Result<Club> detail(@PathVariable Long id, HttpServletRequest request) {
        Club club = clubService.getById(id);
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        if (securityContext.isStudent(request) && !"approved".equals(club.getStatus())) {
            throw new RuntimeException("社团尚未通过审批");
        }
        if (!securityContext.isAdmin(request) && !securityContext.isStudent(request)) {
            securityContext.requireClubManager(request, club);
        }
        return Result.ok(club);
    }

    @PostMapping("/clubs")
    @Transactional
    public Result<Club> create(@Valid @RequestBody ClubRequest requestBody, HttpServletRequest request) {
        Club club = requestBody.toClub();
        Integer applicantId = securityContext.currentUserId(request);
        if (securityContext.isAdmin(request)) {
            if (club.getStatus() == null || club.getStatus().isBlank()) {
                club.setStatus("approved");
            }
        } else {
            if (!securityContext.isStudent(request)) {
                throw new RuntimeException("只有学生可以提交社团成立申请");
            }
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
    public Result<Club> update(@PathVariable Integer id, @Valid @RequestBody ClubRequest requestBody, HttpServletRequest request) {
        Club club = requestBody.toClub();
        Club existing = clubService.getById(id.longValue());
        if (existing == null) {
            throw new RuntimeException("社团不存在");
        }
        if (!securityContext.isAdmin(request)) {
            securityContext.requireClubLeader(request, existing);
            club.setAdvisorId(existing.getAdvisorId());
            club.setStatus(existing.getStatus());
        }
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
            Integer clubId = securityContext.managedClubId(request);
            boolean includeAdvisor = securityContext.isTeacher(request);
            return Result.ok(PageResult.of(clubService.searchManageable(keyword, userId, clubId, includeAdvisor, page, size), clubService.searchManageableCount(keyword, userId, clubId, includeAdvisor), page, size));
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
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        if (securityContext.isStudent(request) && !"approved".equals(club.getStatus())) {
            throw new RuntimeException("社团尚未通过审批");
        }
        if (!securityContext.isStudent(request)) {
            securityContext.requireClubManager(request, club);
        }
        java.util.List<ClubMember> members = clubMemberService.listByClub(id, page, size);
        if (securityContext.isStudent(request)) {
            members = members.stream()
                    .map(this::maskPublicMember)
                    .toList();
        }
        return Result.ok(PageResult.of(members, clubMemberService.countByClub(id), page, size));
    }

    @PostMapping("/clubs/{id}/members")
    public Result<ClubMember> addMember(
            @PathVariable Integer id,
            HttpServletRequest request) {
        throw new RuntimeException("成员加入必须通过入社申请审核，不能直接添加成员");
    }

    @PutMapping("/clubs/{id}/members/{userId}")
    public Result<ClubMember> updateMember(
            @PathVariable Integer id,
            @PathVariable Integer userId,
            @RequestBody ClubMemberUpdateRequest body,
            HttpServletRequest request) {
        securityContext.requireClubLeader(request, clubService.getById(id.longValue()));
        return Result.ok(clubMemberService.updateMember(id, userId, body.role(), body.status()));
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
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        if (securityContext.isStudent(request) && !"approved".equals(club.getStatus())) {
            throw new RuntimeException("社团尚未通过审批");
        }
        if (!securityContext.isStudent(request)) {
            securityContext.requireClubManager(request, club);
        }
        String status = securityContext.isStudent(request) ? "approved" : null;
        return Result.ok(PageResult.of(clubService.getActivities(id.longValue(), status, page, size), clubService.getActivityCount(id.longValue(), status), page, size));
    }

    private ClubMember maskPublicMember(ClubMember member) {
        ClubMember masked = new ClubMember();
        masked.setId(member.getId());
        masked.setClubId(member.getClubId());
        masked.setRole(member.getRole());
        masked.setStatus(member.getStatus());
        masked.setJoinTime(member.getJoinTime());
        masked.setClubName(member.getClubName());
        masked.setUserName(member.getUserName());
        return masked;
    }
}
