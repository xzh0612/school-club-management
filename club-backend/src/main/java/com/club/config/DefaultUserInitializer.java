package com.club.config;

import com.club.entity.User;
import com.club.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.bootstrap.default-users.enabled:false}")
    private boolean defaultUsersEnabled;

    @Value("${app.bootstrap.default-users.password:}")
    private String defaultUserPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (!defaultUsersEnabled) {
            return;
        }
        if (defaultUserPassword == null || defaultUserPassword.isBlank()) {
            throw new RuntimeException("启用默认用户初始化时必须配置 app.bootstrap.default-users.password");
        }

        ensureUser("admin", defaultUserPassword, "系统管理员", "admin", "admin@school.edu.cn", "13800138000");
        ensureUser("teacher1", defaultUserPassword, "张老师", "teacher", "zhang@school.edu.cn", "13800138001");
        ensureUser("leader1", defaultUserPassword, "社团负责人", "club_leader", "leader@school.edu.cn", "13800138002");
        ensureUser("student1", defaultUserPassword, "普通学生", "student", "student@school.edu.cn", "13800138003");
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
