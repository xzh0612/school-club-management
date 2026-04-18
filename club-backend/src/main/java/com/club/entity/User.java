package com.club.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer userId;        // 用户ID
    private String username;       // 用户名
    private String password;       // 密码
    private String realName;       // 真实姓名
    private String role;           // 角色: student, club_leader, teacher, admin
    private String status;         // 状态: active, inactive
    private Integer clubId;        // 所属社团ID
    private String studentId;      // 学号/工号
    private String department;     // 院系
    private String className;      // 班级
    private java.util.Date registerTime;  // 注册时间
    private LocalDateTime lastLoginTime;   // 最后登录时间
    private String email;          // 邮箱
    private String phone;          // 电话
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
    
    // 为了兼容前端，提供id属性
    public Integer getId() {
        return userId;
    }
    
    public void setId(Integer id) {
        this.userId = id;
    }
}