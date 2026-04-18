package com.club.service;

import com.club.entity.*;
import java.util.List;

public interface ActivityService {
    List<Activity> list(String status, Long clubId, int page, int size);
    int count(String status, Long clubId);
    Activity getById(Integer id);
    List<Activity> byClubId(Long clubId);
    Activity create(Activity activity);
    Activity update(Activity activity);
    void delete(Integer id);
    List<Activity> search(String keyword, int page, int size);
    int searchCount(String keyword);
    // TODO: 实现报名相关功能
    // void signup(ActivitySignup signup);
    // void cancelSignup(Integer activityId, Integer userId);
    // List<User> getSignups(Integer activityId, int page, int size);
    // int getSignupCount(Integer activityId);
}