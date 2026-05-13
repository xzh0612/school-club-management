package com.club.service;

import com.club.entity.*;
import java.util.List;

public interface ActivityService {
    List<Activity> list(String status, Long clubId, int page, int size);
    List<Activity> listManageable(Integer userId, Integer clubId, int page, int size);
    int count(String status, Long clubId);
    int countManageable(Integer userId, Integer clubId);
    Activity getById(Integer id);
    List<Activity> byClubId(Long clubId);
    Activity create(Activity activity);
    Activity update(Activity activity);
    void delete(Integer id);
    List<Activity> search(String keyword, int page, int size);
    int searchCount(String keyword);
    List<Activity> searchByStatus(String keyword, String status, int page, int size);
    int searchCountByStatus(String keyword, String status);
    List<Activity> searchManageable(String keyword, Integer userId, Integer clubId, int page, int size);
    int searchManageableCount(String keyword, Integer userId, Integer clubId);
    void signup(Integer activityId, Integer userId);
    void cancelSignup(Integer activityId, Integer userId);
    List<ActivitySignup> getSignups(Integer activityId, int page, int size);
    int getSignupCount(Integer activityId);
}
