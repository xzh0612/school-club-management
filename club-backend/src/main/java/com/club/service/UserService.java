package com.club.service;

import com.club.entity.User;
import java.util.List;

public interface UserService {
    com.club.entity.LoginVO login(com.club.entity.LoginDTO dto);
    User getById(Integer userId);
    List<User> list(String role, int page, int size);
    int count(String role);
    User create(User user);
    User update(User user);
    void delete(Integer userId);
    List<User> search(String keyword, int page, int size);
    int searchCount(String keyword);
}