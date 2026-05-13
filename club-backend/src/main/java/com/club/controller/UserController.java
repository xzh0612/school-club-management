package com.club.controller;

import com.club.common.*;
import com.club.entity.*;
import com.club.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityContext securityContext;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            HttpServletRequest request) {
        if (!securityContext.isAdmin(request)) {
            Integer managedClubId = securityContext.currentUser(request).getClubId();
            return Result.ok(PageResult.of(userService.listByClub(managedClubId, page, size), userService.countByClub(managedClubId), page, size));
        }
        return Result.ok(PageResult.of(userService.list(role, page, size), userService.count(role), page, size));
    }

    @PostMapping("/users")
    public Result<User> create(@RequestBody User user, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        return Result.ok(userService.create(user));
    }

    @PutMapping("/users/{id}")
    public Result<User> update(@PathVariable Integer id, @RequestBody Map<String, Object> updates, HttpServletRequest request) {
        Integer currentUserId = securityContext.currentUserId(request);
        User existingUser = userService.getById(id);
        if (existingUser == null) return Result.error("用户不存在");
        validateUserUpdateScope(existingUser, updates, currentUserId.equals(id), request);

        if (updates.containsKey("username")) existingUser.setUsername((String) updates.get("username"));
        if (updates.containsKey("realName")) existingUser.setRealName((String) updates.get("realName"));
        if (updates.containsKey("password") && updates.get("password") != null) {
            existingUser.setPassword(passwordEncoder.encode((String) updates.get("password")));
        }
        if (updates.containsKey("studentId")) existingUser.setStudentId((String) updates.get("studentId"));
        if (updates.containsKey("department")) existingUser.setDepartment((String) updates.get("department"));
        if (updates.containsKey("className")) existingUser.setClassName((String) updates.get("className"));
        if (updates.containsKey("role")) {
            securityContext.requireAdmin(request);
            existingUser.setRole((String) updates.get("role"));
        }
        if (updates.containsKey("status")) existingUser.setStatus((String) updates.get("status"));
        if (updates.containsKey("email")) existingUser.setEmail((String) updates.get("email"));
        if (updates.containsKey("phone")) existingUser.setPhone((String) updates.get("phone"));

        return Result.ok(userService.update(existingUser));
    }

    private void validateUserUpdateScope(User target, Map<String, Object> updates, boolean selfUpdate, HttpServletRequest request) {
        if (selfUpdate) {
            ensureAllowedFields(updates, Set.of("realName", "password", "department", "className", "email", "phone"));
            return;
        }

        securityContext.requireAdmin(request);
        if (isClubMemberAccount(target)) {
            throw new RuntimeException("普通社员资料不能在管理员用户管理中修改，请由本人或社团负责人维护");
        }
        ensureAllowedFields(updates, Set.of("username", "realName", "password", "role", "status", "email", "phone"));
    }

    private boolean isClubMemberAccount(User user) {
        return "student".equals(user.getRole()) || "club_leader".equals(user.getRole());
    }

    private void ensureAllowedFields(Map<String, Object> updates, Set<String> allowedFields) {
        for (String field : updates.keySet()) {
            if (!allowedFields.contains(field)) {
                throw new RuntimeException("无权修改字段：" + field);
            }
        }
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        userService.delete(id);
        return Result.ok();
    }

    @GetMapping("/users/search")
    public Result<PageResult<User>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        securityContext.requireAdmin(request);
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
    public Result<java.util.List<Map<String, Object>>> getUserClubs(@PathVariable Integer userId, HttpServletRequest request) {
        Integer currentUserId = securityContext.currentUserId(request);
        if (!securityContext.isAdmin(request) && !currentUserId.equals(userId)) {
            throw new RuntimeException("只能查看自己的社团信息");
        }
        return Result.ok(userService.getUserClubs(userId));
    }
    
    /**
     * 获取用户活动记录
     */
    @GetMapping("/users/{userId}/activities")
    public Result<java.util.List<Map<String, Object>>> getUserActivities(@PathVariable Integer userId, HttpServletRequest request) {
        Integer currentUserId = securityContext.currentUserId(request);
        if (!securityContext.isAdmin(request) && !currentUserId.equals(userId)) {
            throw new RuntimeException("只能查看自己的活动记录");
        }
        return Result.ok(userService.getUserActivities(userId));
    }

}
