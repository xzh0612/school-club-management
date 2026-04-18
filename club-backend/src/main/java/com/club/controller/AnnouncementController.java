package com.club.controller;

import com.club.common.*;
import com.club.entity.*;
import com.club.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/announcements")
    public Result<PageResult<Announcement>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Boolean isTop) {
        return Result.ok(PageResult.of(announcementService.list(targetType, isTop, page, size), announcementService.count(targetType, isTop), page, size));
    }

    @GetMapping("/announcements/{id}")
    public Result<Announcement> detail(@PathVariable Integer id) {
        return Result.ok(announcementService.getById(id));
    }


    @PostMapping("/announcements")
    public Result<Announcement> create(@RequestBody Announcement announcement) {
        return Result.ok(announcementService.create(announcement));
    }

    @PutMapping("/announcements/{id}")
    public Result<Announcement> update(@PathVariable Integer id, @RequestBody Announcement announcement) {
        announcement.setAnnouncementId(id);
        return Result.ok(announcementService.update(announcement));
    }

    @DeleteMapping("/announcements/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        announcementService.delete(id);
        return Result.ok();
    }

    @GetMapping("/announcements/search")
    public Result<PageResult<Announcement>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(PageResult.of(announcementService.search(keyword, page, size), announcementService.searchCount(keyword), page, size));
    }

    // 置顶公告
    @PutMapping("/announcements/{id}/top")
    public Result<Void> setTop(@PathVariable Integer id, @RequestParam Boolean isTop) {
        announcementService.setTop(id, isTop);
        return Result.ok();
    }

    // 发布公告
    @PutMapping("/announcements/{id}/publish")
    public Result<Void> publish(@PathVariable Integer id) {
        announcementService.publish(id);
        return Result.ok();
    }

    // 撤销公告
    @PutMapping("/announcements/{id}/revoke")
    public Result<Void> revoke(@PathVariable Integer id) {
        announcementService.revoke(id);
        return Result.ok();
    }
}