package com.club.controller;

import com.club.common.*;
import com.club.entity.*;
import com.club.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @GetMapping("/clubs")
    public Result<PageResult<Club>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return Result.ok(PageResult.of(clubService.list(status, keyword, page, size), clubService.count(keyword), page, size));
    }

    @GetMapping("/clubs/{id}")
    public Result<Club> detail(@PathVariable Long id) {
        return Result.ok(clubService.getById(id));
    }

    @PostMapping("/clubs")
    public Result<Club> create(@RequestBody Club club) {
        return Result.ok(clubService.create(club));
    }

    @PutMapping("/clubs/{id}")
    public Result<Club> update(@PathVariable Integer id, @RequestBody Club club) {
        club.setClubId(id);
        return Result.ok(clubService.update(club));
    }

    @DeleteMapping("/clubs/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        clubService.delete((long)id);
        return Result.ok();
    }

    @GetMapping("/clubs/search")
    public Result<PageResult<Club>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(PageResult.of(clubService.search(keyword, page, size), clubService.searchCount(keyword), page, size));
    }

    // TODO: 实现以下功能
    // 获取社团成员列表
    // 获取社团活动列表
}