package com.club.controller;

import com.club.common.*;
import com.club.dto.AnnouncementRequest;
import com.club.entity.*;
import com.club.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final ClubMemberService clubMemberService;
    private final SecurityContext securityContext;

    @GetMapping("/announcements")
    public Result<PageResult<Announcement>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Boolean isTop,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        if (securityContext.isAdmin(request)) {
            return Result.ok(PageResult.of(announcementService.list(status, targetType, isTop, page, size), announcementService.count(status, targetType, isTop), page, size));
        }
        if (securityContext.isStudent(request) || securityContext.isTeacher(request)) {
            java.util.List<Integer> clubIds = visibleClubIds(request);
            return Result.ok(PageResult.of(announcementService.listVisible(clubIds, targetType, isTop, page, size), announcementService.countVisible(clubIds, targetType, isTop), page, size));
        }
        if (securityContext.isLeader(request)) {
            Integer clubId = requireLeaderClubId(request);
            return Result.ok(PageResult.of(announcementService.listManageable(clubId, status, isTop, page, size), announcementService.countManageable(clubId, status, isTop), page, size));
        }
        throw new RuntimeException("无权查看公告列表");
    }

    @GetMapping("/announcements/{id}")
    public Result<Announcement> detail(@PathVariable Integer id, HttpServletRequest request) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        requireAnnouncementReadable(request, announcement);
        return Result.ok(announcement);
    }


    @PostMapping("/announcements")
    public Result<Announcement> create(@Valid @RequestBody AnnouncementRequest requestBody, HttpServletRequest request) {
        Announcement announcement = requestBody.toAnnouncement();
        if (!securityContext.isAdmin(request) && !securityContext.isLeader(request)) {
            throw new RuntimeException("只有管理员或社团负责人可以发布公告");
        }
        if (securityContext.isLeader(request)) {
            Integer clubId = requireLeaderClubId(request);
            announcement.setTargetType("club");
            announcement.setTargetId(clubId);
        }
        announcement.setPublisherId(securityContext.currentUserId(request));
        return Result.ok(announcementService.create(announcement));
    }

    @PutMapping("/announcements/{id}")
    public Result<Announcement> update(@PathVariable Integer id, @Valid @RequestBody AnnouncementRequest requestBody, HttpServletRequest request) {
        Announcement announcement = requestBody.toAnnouncement();
        if (!securityContext.isAdmin(request) && !securityContext.isLeader(request)) {
            throw new RuntimeException("只有管理员或社团负责人可以修改公告");
        }
        Announcement existing = announcementService.getById(id);
        requireAnnouncementOwner(request, existing);
        if (securityContext.isLeader(request)) {
            Integer clubId = requireLeaderClubId(request);
            announcement.setTargetType("club");
            announcement.setTargetId(clubId);
        }
        announcement.setAnnouncementId(id);
        return Result.ok(announcementService.update(announcement));
    }

    @DeleteMapping("/announcements/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        if (!securityContext.isAdmin(request) && !securityContext.isLeader(request)) {
            throw new RuntimeException("只有管理员或社团负责人可以删除公告");
        }
        requireAnnouncementOwner(request, announcementService.getById(id));
        announcementService.delete(id);
        return Result.ok();
    }

    @GetMapping("/announcements/search")
    public Result<PageResult<Announcement>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        if (securityContext.isAdmin(request)) {
            return Result.ok(PageResult.of(announcementService.search(keyword, null, page, size), announcementService.searchCount(keyword, null), page, size));
        }
        if (securityContext.isStudent(request) || securityContext.isTeacher(request)) {
            java.util.List<Integer> clubIds = visibleClubIds(request);
            return Result.ok(PageResult.of(announcementService.searchVisible(keyword, clubIds, page, size), announcementService.searchVisibleCount(keyword, clubIds), page, size));
        }
        if (securityContext.isLeader(request)) {
            Integer clubId = requireLeaderClubId(request);
            return Result.ok(PageResult.of(announcementService.searchManageable(keyword, clubId, page, size), announcementService.searchManageableCount(keyword, clubId), page, size));
        }
        throw new RuntimeException("无权搜索公告");
    }

    // 置顶公告
    @PutMapping("/announcements/{id}/top")
    public Result<Void> setTop(@PathVariable Integer id, @RequestParam Boolean isTop, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        announcementService.setTop(id, isTop);
        return Result.ok();
    }

    // 发布公告
    @PutMapping("/announcements/{id}/publish")
    public Result<Void> publish(@PathVariable Integer id, HttpServletRequest request) {
        if (!securityContext.isAdmin(request) && !securityContext.isLeader(request)) {
            throw new RuntimeException("只有管理员或社团负责人可以发布公告");
        }
        requireAnnouncementOwner(request, announcementService.getById(id));
        announcementService.publish(id);
        return Result.ok();
    }

    // 撤销公告
    @PutMapping("/announcements/{id}/revoke")
    public Result<Void> revoke(@PathVariable Integer id, HttpServletRequest request) {
        if (!securityContext.isAdmin(request) && !securityContext.isLeader(request)) {
            throw new RuntimeException("只有管理员或社团负责人可以撤销公告");
        }
        requireAnnouncementOwner(request, announcementService.getById(id));
        announcementService.revoke(id);
        return Result.ok();
    }

    private Integer requireLeaderClubId(HttpServletRequest request) {
        return securityContext.requireLeaderClubId(request);
    }

    private void requireAnnouncementOwner(HttpServletRequest request, Announcement announcement) {
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        if (securityContext.isAdmin(request)) {
            return;
        }
        Integer clubId = requireLeaderClubId(request);
        if (!"club".equals(announcement.getTargetType()) || !clubId.equals(announcement.getTargetId())) {
            throw new RuntimeException("社团负责人只能管理本社团公告");
        }
    }

    private void requireAnnouncementReadable(HttpServletRequest request, Announcement announcement) {
        if (securityContext.isAdmin(request)) {
            return;
        }
        if (securityContext.isLeader(request)) {
            Integer clubId = requireLeaderClubId(request);
            if ("club".equals(announcement.getTargetType()) && clubId.equals(announcement.getTargetId())) {
                return;
            }
        }
        java.util.List<Integer> clubIds = visibleClubIds(request);
        boolean visibleToStudent = "published".equals(announcement.getStatus())
                && ("all".equals(announcement.getTargetType())
                    || ("club".equals(announcement.getTargetType()) && clubIds.contains(announcement.getTargetId())));
        if (!visibleToStudent) {
            throw new RuntimeException("公告不可访问");
        }
    }

    private java.util.List<Integer> visibleClubIds(HttpServletRequest request) {
        return clubMemberService.listActiveClubIdsByUser(securityContext.currentUserId(request));
    }
}
