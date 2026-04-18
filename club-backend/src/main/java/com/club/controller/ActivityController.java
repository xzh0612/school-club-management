package com.club.controller;

import com.club.common.*;
import com.club.entity.*;
import com.club.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/activities")
    public Result<PageResult<Activity>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(PageResult.of(activityService.list(null, null, page, size), activityService.count(null, null), page, size));
    }

    @GetMapping("/activities/{id}")
    public Result<Activity> detail(@PathVariable Integer id) {
        return Result.ok(activityService.getById(id));
    }

    @PostMapping("/activities")
    public Result<Activity> create(@RequestBody Activity activity) {
        return Result.ok(activityService.create(activity));
    }

    @PutMapping("/activities/{id}")
    public Result<Activity> update(@PathVariable Integer id, @RequestBody Activity activity) {
        activity.setActivityId(id);
        return Result.ok(activityService.update(activity));
    }

    @DeleteMapping("/activities/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        activityService.delete(id);
        return Result.ok();
    }
}