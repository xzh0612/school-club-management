package com.club.vo;

import com.club.entity.User;

import java.time.LocalDateTime;
import java.util.Date;

public record UserVO(
        Integer userId,
        Integer id,
        String username,
        String realName,
        String role,
        String status,
        Integer clubId,
        String studentId,
        String department,
        String className,
        Date registerTime,
        LocalDateTime lastLoginTime,
        String email,
        String phone,
        String clubName
) {
    public static UserVO from(User user) {
        if (user == null) {
            return null;
        }
        return new UserVO(
                user.getUserId(),
                user.getUserId(),
                user.getUsername(),
                user.getRealName(),
                user.getRole(),
                user.getStatus(),
                user.getClubId(),
                user.getStudentId(),
                user.getDepartment(),
                user.getClassName(),
                user.getRegisterTime(),
                user.getLastLoginTime(),
                user.getEmail(),
                user.getPhone(),
                user.getClubName()
        );
    }
}
