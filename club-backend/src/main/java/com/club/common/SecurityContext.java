package com.club.common;

import com.club.entity.Club;
import com.club.entity.User;
import com.club.mapper.ClubMemberMapper;
import com.club.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityContext {

    private final UserService userService;
    private final ClubMemberMapper clubMemberMapper;

    public Integer currentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw BusinessException.unauthorized("未登录");
        }
        if (userId instanceof Number number) {
            return number.intValue();
        }
        throw BusinessException.unauthorized("登录身份格式不合法");
    }

    public String currentRole(HttpServletRequest request) {
        User user = currentUser(request);
        return user.getRole() == null ? "" : normalizeRole(user.getRole());
    }

    public User currentUser(HttpServletRequest request) {
        User cachedUser = (User) request.getAttribute("currentUser");
        if (cachedUser != null) {
            return cachedUser;
        }

        User user = userService.getById(currentUserId(request));
        if (user == null) {
            throw BusinessException.unauthorized("当前用户不存在");
        }
        if ("inactive".equals(user.getStatus())) {
            throw BusinessException.forbidden("该账号已被停用");
        }
        request.setAttribute("currentUser", user);
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
            throw BusinessException.forbidden("只有系统管理员可以执行该操作");
        }
    }

    public void requireAdminOrTeacher(HttpServletRequest request) {
        if (!isAdmin(request) && !isTeacher(request)) {
            throw BusinessException.forbidden("只有管理员或指导老师可以执行该操作");
        }
    }

    public void requireClubManager(HttpServletRequest request, Club club) {
        if (club == null) {
            throw BusinessException.notFound("社团不存在");
        }
        if (isAdmin(request)) {
            return;
        }

        User user = currentUser(request);
        Integer userId = user.getUserId();
        Integer managedClubId = user.getClubId();
        boolean advisesClub = isTeacher(request) && userId != null && userId.equals(club.getAdvisorId());
        boolean boundLeader = isLeader(request) && managedClubId != null && managedClubId.equals(club.getClubId());
        boolean leadsByMembership = isLeader(request) && userId != null
                && clubMemberMapper.countActiveLeaderMembership(club.getClubId(), userId) > 0;

        if (advisesClub || boundLeader || leadsByMembership) {
            return;
        }

        throw BusinessException.forbidden("无权管理该社团");
    }

    public void requireClubLeader(HttpServletRequest request, Club club) {
        if (club == null) {
            throw BusinessException.notFound("社团不存在");
        }
        if (isAdmin(request)) {
            return;
        }

        User user = currentUser(request);
        Integer userId = user.getUserId();
        Integer managedClubId = user.getClubId();
        if (!isLeader(request)) {
            throw BusinessException.forbidden("只有社团负责人可以执行该操作");
        }

        boolean leadsClub = managedClubId != null && managedClubId.equals(club.getClubId());
        boolean leadsByMembership = userId != null
                && clubMemberMapper.countActiveLeaderMembership(club.getClubId(), userId) > 0;

        if (leadsClub || leadsByMembership) {
            return;
        }

        throw BusinessException.forbidden("只有社团负责人可以执行该操作");
    }

    public Integer managedClubId(HttpServletRequest request) {
        if (isAdmin(request) || isTeacher(request)) {
            return null;
        }
        User user = currentUser(request);
        if (user.getClubId() != null) {
            return user.getClubId();
        }
        return leaderClubIds(request).stream().findFirst().orElse(null);
    }

    public List<Integer> leaderClubIds(HttpServletRequest request) {
        return clubMemberMapper.findActiveLeaderClubIdsByUser(currentUserId(request));
    }

    public Integer requireLeaderClubId(HttpServletRequest request) {
        if (!isLeader(request)) {
            throw BusinessException.forbidden("只有社团负责人可以执行该操作");
        }
        Integer clubId = managedClubId(request);
        if (clubId == null) {
            throw BusinessException.forbidden("当前负责人未绑定可管理社团");
        }
        return clubId;
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
