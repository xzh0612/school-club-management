package com.club.config;

import com.club.entity.User;
import com.club.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultUserInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        ensureUser("admin", "123456", "系统管理员", "admin", "admin@school.edu.cn", "13800138000");
        ensureUser("teacher1", "123456", "张老师", "teacher", "zhang@school.edu.cn", "13800138001");
        ensureUser("leader1", "123456", "社团负责人", "club_leader", "leader@school.edu.cn", "13800138002");
        ensureUser("student1", "123456", "普通学生", "student", "student@school.edu.cn", "13800138003");
    }

    private void ensureUser(String username, String password, String realName, String role, String email, String phone) {
        if (userMapper.findByUsername(username) != null) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setRole(role);
        user.setStatus("active");
        user.setEmail(email);
        user.setPhone(phone);
        userMapper.insert(user);

        log.info("[DefaultUserInitializer] 已初始化默认用户: {}", username);
    }
}
