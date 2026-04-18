package com.club.controller;

import com.club.common.*;
import com.club.entity.*;
import com.club.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role) {
        return Result.ok(PageResult.of(userService.list(role, page, size), userService.count(role), page, size));
    }

    @PostMapping("/users")
    public Result<User> create(@RequestBody User user) {
        return Result.ok(userService.create(user));
    }

    @PutMapping("/users/{id}")
    public Result<User> update(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        User existingUser = userService.getById(id);
        if (existingUser == null) return Result.error("用户不存在");

        if (updates.containsKey("username")) existingUser.setUsername((String) updates.get("username"));
        if (updates.containsKey("realName")) existingUser.setRealName((String) updates.get("realName"));
        if (updates.containsKey("password") && updates.get("password") != null) {
            existingUser.setPassword(passwordEncoder.encode((String) updates.get("password")));
        }
        if (updates.containsKey("role")) existingUser.setRole((String) updates.get("role"));
        if (updates.containsKey("email")) existingUser.setEmail((String) updates.get("email"));
        if (updates.containsKey("phone")) existingUser.setPhone((String) updates.get("phone"));

        return Result.ok(userService.update(existingUser));
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return Result.ok();
    }

    @GetMapping("/users/search")
    public Result<PageResult<User>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(PageResult.of(userService.search(keyword, page, size), userService.searchCount(keyword), page, size));
    }
    
    @GetMapping("/users/current")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(userService.getById(userId.intValue()));
    }
    
    /**
     * 获取用户个人社团信息
     */
    @GetMapping("/users/{userId}/clubs")
    public Result<java.util.List<Map<String, Object>>> getUserClubs(@PathVariable Integer userId) {
        // 模拟用户社团数据
        java.util.List<Map<String, Object>> clubs = new java.util.ArrayList<>();
        
        Map<String, Object> club1 = new java.util.HashMap<>();
        club1.put("id", 1);
        club1.put("clubName", "篮球社");
        club1.put("role", "核心成员");
        club1.put("joinTime", new java.util.Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));
        clubs.add(club1);
        
        Map<String, Object> club2 = new java.util.HashMap<>();
        club2.put("id", 2);
        club2.put("clubName", "计算机协会");
        club2.put("role", "普通成员");
        club2.put("joinTime", new java.util.Date(System.currentTimeMillis() - 45L * 24 * 60 * 60 * 1000));
        clubs.add(club2);
        
        return Result.ok(clubs);
    }
    
    /**
     * 获取用户活动记录
     */
    @GetMapping("/users/{userId}/activities")
    public Result<java.util.List<Map<String, Object>>> getUserActivities(@PathVariable Integer userId) {
        // 模拟用户活动记录
        java.util.List<Map<String, Object>> activities = new java.util.ArrayList<>();
        
        Map<String, Object> activity1 = new java.util.HashMap<>();
        activity1.put("id", 1);
        activity1.put("activityName", "迎新篮球赛");
        activity1.put("clubName", "篮球社");
        activity1.put("activityTime", new java.util.Date(System.currentTimeMillis() - 5L * 24 * 60 * 60 * 1000));
        activity1.put("checkStatus", "已签到");
        activity1.put("rating", "⭐⭐⭐⭐⭐");
        activities.add(activity1);
        
        Map<String, Object> activity2 = new java.util.HashMap<>();
        activity2.put("id", 2);
        activity2.put("activityName", "编程入门讲座");
        activity2.put("clubName", "计算机协会");
        activity2.put("activityTime", new java.util.Date(System.currentTimeMillis() - 10L * 24 * 60 * 60 * 1000));
        activity2.put("checkStatus", "已签到");
        activity2.put("rating", "⭐⭐⭐⭐");
        activities.add(activity2);
        
        Map<String, Object> activity3 = new java.util.HashMap<>();
        activity3.put("id", 3);
        activity3.put("activityName", "春季招新");
        activity3.put("clubName", "篮球社");
        activity3.put("activityTime", new java.util.Date(System.currentTimeMillis() + 2L * 24 * 60 * 60 * 1000));
        activity3.put("checkStatus", "已报名");
        activity3.put("rating", "");
        activities.add(activity3);
        
        return Result.ok(activities);
    }

}