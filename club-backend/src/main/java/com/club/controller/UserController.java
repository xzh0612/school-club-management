package com.club.controller;

import com.club.common.*;
import com.club.dto.UserCreateRequest;
import com.club.dto.UserUpdateRequest;
import com.club.entity.*;
import com.club.service.*;
import com.club.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    public Result<PageResult<UserVO>> list(
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
            return Result.ok(PageResult.of(toUserVOs(userService.listByClub(managedClubId, page, size)), userService.countByClub(managedClubId), page, size));
        }
        return Result.ok(PageResult.of(toUserVOs(userService.list(role, status, page, size)), userService.count(role, status), page, size));
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserVO> create(@Valid @RequestBody UserCreateRequest requestBody, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        User user = requestBody.toUser();
        user.setRole(requireAllowedValue(user.getRole(), USER_ROLES, "角色"));
        if (user.getStatus() != null && !user.getStatus().isBlank()) {
            user.setStatus(requireAllowedValue(user.getStatus(), USER_STATUSES, "账号状态"));
        }
        return Result.ok(UserVO.from(userService.create(user)));
    }

    @PutMapping("/users/{id}")
    public Result<UserVO> update(@PathVariable Integer id, @Valid @RequestBody UserUpdateRequest updates, HttpServletRequest request) {
        Integer currentUserId = securityContext.currentUserId(request);
        User existingUser = userService.getById(id);
        if (existingUser == null) return Result.error("用户不存在");
        Set<String> updateFields = updateFields(updates);
        validateUserUpdateScope(existingUser, updateFields, currentUserId.equals(id), request);

        if (updates.username() != null) existingUser.setUsername(nonBlankString(updates.username(), "username"));
        if (updates.realName() != null) existingUser.setRealName(stringValue(updates.realName(), "realName"));
        if (updates.password() != null) existingUser.setPassword(passwordEncoder.encode(nonBlankString(updates.password(), "password")));
        if (updates.studentId() != null) existingUser.setStudentId(nullableString(updates.studentId(), "studentId"));
        if (updates.department() != null) existingUser.setDepartment(nullableString(updates.department(), "department"));
        if (updates.className() != null) existingUser.setClassName(nullableString(updates.className(), "className"));
        if (updates.role() != null) {
            securityContext.requireAdmin(request);
            existingUser.setRole(requireAllowedValue(updates.role(), USER_ROLES, "角色"));
        }
        if (updates.status() != null) existingUser.setStatus(requireAllowedValue(updates.status(), USER_STATUSES, "账号状态"));
        if (updates.email() != null) existingUser.setEmail(nullableString(updates.email(), "email"));
        if (updates.phone() != null) existingUser.setPhone(nullableString(updates.phone(), "phone"));

        boolean invalidateTokens = updateFields.contains("password") || updateFields.contains("role") || updateFields.contains("status");
        User updated = userService.update(existingUser);
        if (invalidateTokens) {
            userService.invalidateTokens(id);
        }
        return Result.ok(UserVO.from(updated));
    }

    private void validateUserUpdateScope(User target, Set<String> updates, boolean selfUpdate, HttpServletRequest request) {
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

    private void ensureAllowedFields(Set<String> updates, Set<String> allowedFields) {
        for (String field : updates) {
            if (!allowedFields.contains(field)) {
                throw new RuntimeException("无权修改字段：" + field);
            }
        }
    }

    private Set<String> updateFields(UserUpdateRequest updates) {
        Set<String> fields = new java.util.HashSet<>();
        if (updates.username() != null) fields.add("username");
        if (updates.realName() != null) fields.add("realName");
        if (updates.password() != null) fields.add("password");
        if (updates.studentId() != null) fields.add("studentId");
        if (updates.department() != null) fields.add("department");
        if (updates.className() != null) fields.add("className");
        if (updates.role() != null) fields.add("role");
        if (updates.status() != null) fields.add("status");
        if (updates.email() != null) fields.add("email");
        if (updates.phone() != null) fields.add("phone");
        return fields;
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
    public Result<PageResult<UserVO>> search(
            @RequestParam String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        securityContext.requireAdmin(request);
        return Result.ok(PageResult.of(toUserVOs(userService.search(keyword, role, status, page, size)), userService.searchCount(keyword, role, status), page, size));
    }
    
    @GetMapping("/users/current")
    public Result<UserVO> getCurrentUser(HttpServletRequest request) {
        return Result.ok(UserVO.from(securityContext.currentUser(request)));
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

    private java.util.List<UserVO> toUserVOs(java.util.List<User> users) {
        return users.stream().map(UserVO::from).toList();
    }

}
