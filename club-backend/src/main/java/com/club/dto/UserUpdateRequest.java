package com.club.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(max = 50, message = "用户名不能超过50字")
        String username,
        @Size(max = 50, message = "姓名不能超过50字")
        String realName,
        @Size(min = 6, max = 100, message = "密码长度必须在6到100字之间")
        String password,
        @Size(max = 30, message = "学号不能超过30字")
        String studentId,
        @Size(max = 100, message = "院系不能超过100字")
        String department,
        @Size(max = 50, message = "班级不能超过50字")
        String className,
        String role,
        String status,
        @Email(message = "邮箱格式不正确")
        @Size(max = 100, message = "邮箱不能超过100字")
        String email,
        @Pattern(regexp = "^$|^[0-9+\\- ]{6,20}$", message = "手机号格式不正确")
        String phone
) {
}
