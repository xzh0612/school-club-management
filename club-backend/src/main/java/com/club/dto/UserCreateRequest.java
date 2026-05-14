package com.club.dto;

import com.club.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(max = 50, message = "用户名不能超过50字")
        String username,
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 100, message = "密码长度必须在6到100字之间")
        String password,
        @NotBlank(message = "姓名不能为空")
        @Size(max = 50, message = "姓名不能超过50字")
        String realName,
        @NotBlank(message = "角色不能为空")
        String role,
        String status,
        @Size(max = 30, message = "学号不能超过30字")
        String studentId,
        @Size(max = 100, message = "院系不能超过100字")
        String department,
        @Size(max = 50, message = "班级不能超过50字")
        String className,
        @Email(message = "邮箱格式不正确")
        @Size(max = 100, message = "邮箱不能超过100字")
        String email,
        @Pattern(regexp = "^$|^[0-9+\\- ]{6,20}$", message = "手机号格式不正确")
        String phone
) {
    public User toUser() {
        User user = new User();
        user.setUsername(username == null ? null : username.trim());
        user.setPassword(password == null ? null : password.trim());
        user.setRealName(realName == null ? null : realName.trim());
        user.setRole(role);
        user.setStatus(status);
        user.setStudentId(studentId);
        user.setDepartment(department);
        user.setClassName(className);
        user.setEmail(email);
        user.setPhone(phone);
        return user;
    }
}
