package com.club.common;

import com.club.entity.Club;
import com.club.entity.User;
import com.club.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityContext {

    private final UserService userService;

    public Integer currentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        return userId.intValue();
    }

    public String currentRole(HttpServletRequest request) {
        Object role = request.getAttribute("role");
        return role == null ? "" : normalizeRole(role.toString());
    }

    public User currentUser(HttpServletRequest request) {
        User user = userService.getById(currentUserId(request));
        if (user == null) {
            throw new RuntimeException("当前用户不存在");
        }
        return user;
    }

    public boolean isAdmin(HttpServletRequest request) {
        return "admin".equals(currentRole(request));
    }

    public boolean isTeacher(HttpServletRequest request) {
        return "teacher".equals(currentRole(request));
    }

    public boolean isLeader(HttpServletRequest request) {
        return "club_leader".equals(currentRole(request));
    }

    public boolean isStudent(HttpServletRequest request) {
        return "student".equals(currentRole(request));
    }

    public void requireAdmin(HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new RuntimeException("只有系统管理员可以执行该操作");
        }
    }

    public void requireAdminOrTeacher(HttpServletRequest request) {
        if (!isAdmin(request) && !isTeacher(request)) {
            throw new RuntimeException("只有管理员或指导老师可以执行该操作");
        }
    }

    public void requireClubManager(HttpServletRequest request, Club club) {
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        if (isAdmin(request)) {
            return;
        }

        User user = currentUser(request);
        Integer userId = user.getUserId();
        Integer managedClubId = user.getClubId();
        boolean ownsClub = userId != null && userId.equals(club.getFounderId());
        boolean boundToClub = managedClubId != null && managedClubId.equals(club.getClubId());
        boolean advisesClub = isTeacher(request) && userId != null && userId.equals(club.getAdvisorId());

        if (ownsClub || boundToClub || advisesClub) {
            return;
        }

        throw new RuntimeException("无权管理该社团");
    }

    public Integer managedClubId(HttpServletRequest request) {
        if (isAdmin(request) || isTeacher(request)) {
            return null;
        }
        User user = currentUser(request);
        return user.getClubId();
    }

    private String normalizeRole(String role) {
        return switch (role) {
            case "admin", "ADMIN", "系统管理员" -> "admin";
            case "teacher", "TEACHER", "指导老师" -> "teacher";
            case "leader", "LEADER", "club_leader", "CLUB_LEADER", "社团负责人", "社团管理员" -> "club_leader";
            case "student", "STUDENT", "普通学生" -> "student";
            default -> role;
        };
    }
}
