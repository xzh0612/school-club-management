/*
package com.club.config;

import com.club.entity.User;
import com.club.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

*/
/**
 * 一次性密码迁移：将数据库中的明文密码全部替换为 BCrypt 哈希。
 * 迁移完成后请删除此类，避免每次启动都执行检查。
 *//*

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordMigrationRunner implements ApplicationRunner {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        List<User> users = userMapper.findAll(0, Integer.MAX_VALUE);
        int migrated = 0;

        for (User user : users) {
            String pwd = user.getPassword();
            // BCrypt 哈希以 $2a$ 或 $2b$ 开头，跳过已加密的
            if (pwd != null && !pwd.startsWith("$2")) {
                String hashed = passwordEncoder.encode(pwd);
                jdbcTemplate.update(
                    "UPDATE users SET password = ? WHERE user_id = ?",
                    hashed, user.getUserId()
                );
                migrated++;
            }
        }

        if (migrated > 0) {
            log.info("[PasswordMigration] 已将 {} 个用户的明文密码迁移为 BCrypt 哈希，迁移完成后请删除 PasswordMigrationRunner。", migrated);
        } else {
            log.info("[PasswordMigration] 所有用户密码已是 BCrypt 格式，无需迁移。");
        }
    }
}
*/
