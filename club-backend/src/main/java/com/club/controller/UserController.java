package com.club.controller;

import com.club.common.*;
import com.club.entity.*;
import com.club.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private static final Set<String> USER_ROLES = Set.of("admin", "teacher", "club_leader", "student");
    private static final Set<String> USER_STATUSES = Set.of("active", "inactive");

    @GetMapping("/users")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        if (!securityContext.isAdmin(request)) {
            if (!securityContext.isLeader(request)) {
                throw new RuntimeException("只有管理员或社团负责人可以查看用户列表");
            }
            Integer managedClubId = securityContext.requireLeaderClubId(request);
            return Result.ok(PageResult.of(userService.listByClub(managedClubId, page, size), userService.countByClub(managedClubId), page, size));
        }
        return Result.ok(PageResult.of(userService.list(role, status, page, size), userService.count(role, status), page, size));
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> create(@RequestBody User user, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("密码不能为空");
        }
        user.setUsername(user.getUsername().trim());
        user.setPassword(user.getPassword().trim());
        user.setRole(requireAllowedValue(user.getRole(), USER_ROLES, "角色"));
        if (user.getStatus() != null && !user.getStatus().isBlank()) {
            user.setStatus(requireAllowedValue(user.getStatus(), USER_STATUSES, "账号状态"));
        }
        return Result.ok(userService.create(user));
    }

    @PutMapping("/users/{id}")
    public Result<User> update(@PathVariable Integer id, @RequestBody Map<String, Object> updates, HttpServletRequest request) {
        Integer currentUserId = securityContext.currentUserId(request);
        User existingUser = userService.getById(id);
        if (existingUser == null) return Result.error("用户不存在");
        validateUserUpdateScope(existingUser, updates, currentUserId.equals(id), request);

        if (updates.containsKey("username")) existingUser.setUsername(nonBlankString(updates.get("username"), "username"));
        if (updates.containsKey("realName")) existingUser.setRealName(stringValue(updates.get("realName"), "realName"));
        if (updates.containsKey("password") && updates.get("password") != null) {
            String password = stringValue(updates.get("password"), "password");
            if (password.isBlank()) {
                throw new RuntimeException("密码不能为空");
            }
            existingUser.setPassword(passwordEncoder.encode(password));
        }
        if (updates.containsKey("studentId")) existingUser.setStudentId(nullableString(updates.get("studentId"), "studentId"));
        if (updates.containsKey("department")) existingUser.setDepartment(nullableString(updates.get("department"), "department"));
        if (updates.containsKey("className")) existingUser.setClassName(nullableString(updates.get("className"), "className"));
        if (updates.containsKey("role")) {
            securityContext.requireAdmin(request);
            existingUser.setRole(requireAllowedValue(updates.get("role"), USER_ROLES, "角色"));
        }
        if (updates.containsKey("status")) existingUser.setStatus(requireAllowedValue(updates.get("status"), USER_STATUSES, "账号状态"));
        if (updates.containsKey("email")) existingUser.setEmail(nullableString(updates.get("email"), "email"));
        if (updates.containsKey("phone")) existingUser.setPhone(nullableString(updates.get("phone"), "phone"));

        boolean invalidateTokens = updates.containsKey("password") || updates.containsKey("role") || updates.containsKey("status");
        User updated = userService.update(existingUser);
        if (invalidateTokens) {
            userService.invalidateTokens(id);
        }
        return Result.ok(updated);
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
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Integer id, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        if (isClubMemberAccount(existingUser)) {
            throw new RuntimeException("普通社员或社团负责人账号不能在管理员用户管理中直接删除，可先停用账号或由社团负责人维护成员关系");
        }
        userService.delete(id);
        return Result.ok();
    }

    @GetMapping("/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<User>> search(
            @RequestParam String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        securityContext.requireAdmin(request);
        return Result.ok(PageResult.of(userService.search(keyword, role, status, page, size), userService.searchCount(keyword, role, status), page, size));
    }
    
    @GetMapping("/users/current")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        return Result.ok(securityContext.currentUser(request));
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

    private String requireAllowedValue(Object rawValue, Set<String> allowedValues, String fieldName) {
        String value = stringValue(rawValue, fieldName);
        if (!allowedValues.contains(value)) {
            throw new RuntimeException(fieldName + "不合法：" + value);
        }
        return value;
    }

    private String stringValue(Object rawValue, String fieldName) {
        if (!(rawValue instanceof String value)) {
            throw new RuntimeException(fieldName + "格式不合法");
        }
        return value.trim();
    }

    private String nonBlankString(Object rawValue, String fieldName) {
        String value = stringValue(rawValue, fieldName);
        if (value.isBlank()) {
            throw new RuntimeException(fieldName + "不能为空");
        }
        return value;
    }

    private String nullableString(Object rawValue, String fieldName) {
        if (rawValue == null) {
            return null;
        }
        return stringValue(rawValue, fieldName);
    }

}
