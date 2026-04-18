package com.club.service.impl;

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
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new RuntimeException("密码错误");

        LoginVO vo = new LoginVO();
        vo.setId(user.getUserId().longValue());
        vo.setUsername(user.getUsername());
        vo.setName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setToken(jwtUtil.generateToken(user.getUserId().longValue(), user.getUsername(), user.getRole()));
        return vo;
    }

    @Override
    public User getById(Integer userId) { 
        return userMapper.findById(userId); 
    }

    @Override
    public List<User> list(String role, int page, int size) {
        int offset = (page - 1) * size;
        if (role != null && !role.isEmpty()) return userMapper.findByRole(role, offset, size);
        return userMapper.findAll(offset, size);
    }

    @Override
    public int count(String role) {
        if (role != null && !role.isEmpty()) return userMapper.countByRole(role);
        return userMapper.countAll();
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
    public void delete(Integer userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public List<User> search(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        return userMapper.search(keyword, offset, size);
    }

    @Override
    public int searchCount(String keyword) {
        return userMapper.searchCount(keyword);
    }
}