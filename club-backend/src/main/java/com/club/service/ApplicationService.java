package com.club.service;

import com.club.entity.Application;

import java.util.List;

public interface ApplicationService {
    List<Application> list(Integer clubId, String status, Integer currentUserId, boolean adminOrTeacher, int page, int size);
    int count(Integer clubId, String status, Integer currentUserId, boolean adminOrTeacher);
    List<Application> listByUser(Integer userId, int page, int size);
    int countByUser(Integer userId);
    Application getById(Integer id);
    Application submit(Integer userId, Integer clubId, Integer recruitmentId, String introduction);
    void approve(Integer id, Integer reviewerId, String comments);
    void reject(Integer id, Integer reviewerId, String comments);
}
