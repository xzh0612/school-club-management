package com.club.service;

import com.club.entity.*;
import java.util.List;

public interface ClubService {
    List<Club> list(String status, String keyword, int page, int size);
    List<Club> listManageable(Integer userId, Integer clubId, int page, int size);
    int count(String status, String keyword);
    int countManageable(Integer userId, Integer clubId);
    Club getById(Long id);
    Club create(Club club);
    Club update(Club club);
    void delete(Long id);
    List<Club> search(String keyword, int page, int size);
    int searchCount(String keyword);
    List<Club> searchByStatus(String keyword, String status, int page, int size);
    int searchCountByStatus(String keyword, String status);
    List<Club> searchManageable(String keyword, Integer userId, Integer clubId, int page, int size);
    int searchManageableCount(String keyword, Integer userId, Integer clubId);
    List<User> getMembers(Long clubId, int page, int size);
    int getMemberCount(Long clubId);
    List<Activity> getActivities(Long clubId, String status, int page, int size);
    int getActivityCount(Long clubId, String status);
}
