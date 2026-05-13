package com.club.service;

import com.club.entity.User;
import java.util.List;

public interface UserService {
    com.club.entity.LoginVO login(com.club.entity.LoginDTO dto);
    User getById(Integer userId);
    List<User> list(String role, String status, int page, int size);
    List<User> listByClub(Integer clubId, int page, int size);
    int count(String role, String status);
    int countByClub(Integer clubId);
    User create(User user);
    User update(User user);
    void invalidateTokens(Integer userId);
    void delete(Integer userId);
    List<User> search(String keyword, String role, String status, int page, int size);
    int searchCount(String keyword, String role, String status);
    List<java.util.Map<String, Object>> getUserClubs(Integer userId);
    List<java.util.Map<String, Object>> getUserActivities(Integer userId);
}
