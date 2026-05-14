package com.club.service;

import com.club.entity.*;
import java.util.List;

public interface ClubService {
    List<Club> list(String status, String keyword, String clubType, int page, int size);
    List<Club> listManageable(Integer userId, Integer clubId, boolean includeAdvisor, int page, int size);
    int count(String status, String keyword, String clubType);
    int countManageable(Integer userId, Integer clubId, boolean includeAdvisor);
    java.util.Map<String, Integer> stats(Integer userId, Integer clubId, boolean includeAdvisor, boolean global);
    Club getById(Long id);
    Club create(Club club);
    Club update(Club club);
    void delete(Long id);
    List<Club> search(String keyword, int page, int size);
    int searchCount(String keyword);
    List<Club> searchByStatus(String keyword, String status, int page, int size);
    int searchCountByStatus(String keyword, String status);
    List<Club> searchManageable(String keyword, Integer userId, Integer clubId, boolean includeAdvisor, int page, int size);
    int searchManageableCount(String keyword, Integer userId, Integer clubId, boolean includeAdvisor);
    List<User> getMembers(Long clubId, int page, int size);
    int getMemberCount(Long clubId);
    List<Activity> getActivities(Long clubId, String status, int page, int size);
    int getActivityCount(Long clubId, String status);
}
