package com.club.service.impl;

import com.club.common.PageQuery;
import com.club.config.JwtUtil;
import com.club.entity.*;
import com.club.mapper.UserMapper;
import com.club.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) throw new RuntimeException("用户不存在");
        if ("inactive".equals(user.getStatus())) throw new RuntimeException("该账号已被停用");
        if (dto.getRole() != null && !dto.getRole().isBlank() && !dto.getRole().equals(user.getRole())) {
            throw new RuntimeException("所选登录身份与账号角色不匹配");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new RuntimeException("密码错误");
        userMapper.updateLastLoginTime(user.getUserId());

        LoginVO vo = new LoginVO();
        vo.setId(user.getUserId().longValue());
        vo.setUsername(user.getUsername());
        vo.setName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setToken(jwtUtil.generateToken(user.getUserId().longValue(), user.getUsername(), user.getRole(), user.getTokenVersion()));
        return vo;
    }

    @Override
    public User getById(Integer userId) { 
        return userMapper.findById(userId); 
    }

    @Override
    public List<User> list(String role, String status, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        if ((role != null && !role.isEmpty()) || (status != null && !status.isEmpty())) {
            return userMapper.findFiltered(role, status, offset, pageSize);
        }
        return userMapper.findAll(offset, pageSize);
    }

    @Override
    public List<User> listByClub(Integer clubId, int page, int size) {
        if (clubId == null) {
            return List.of();
        }
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return userMapper.findByClubId(clubId, offset, pageSize);
    }

    @Override
    public int count(String role, String status) {
        if ((role != null && !role.isEmpty()) || (status != null && !status.isEmpty())) {
            return userMapper.countFiltered(role, status);
        }
        return userMapper.countAll();
    }

    @Override
    public int countByClub(Integer clubId) {
        if (clubId == null) {
            return 0;
        }
        return userMapper.countByClubId(clubId);
    }

    @Override
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        return user;
    }

    @Override
    public User update(User user) {
        userMapper.update(user);
        return user;
    }

    @Override
    public void invalidateTokens(Integer userId) {
        userMapper.incrementTokenVersion(userId);
    }

    @Override
    public void delete(Integer userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public List<User> search(String keyword, String role, String status, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return userMapper.searchFiltered(keyword, role, status, offset, pageSize);
    }

    @Override
    public int searchCount(String keyword, String role, String status) {
        return userMapper.searchCountFiltered(keyword, role, status);
    }

    @Override
    public List<java.util.Map<String, Object>> getUserClubs(Integer userId) {
        return userMapper.findUserClubs(userId);
    }

    @Override
    public List<java.util.Map<String, Object>> getUserActivities(Integer userId) {
        return userMapper.findUserActivities(userId);
    }
}
