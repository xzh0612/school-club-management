package com.club.service;

import com.club.entity.*;
import java.util.List;

public interface ClubService {
    List<Club> list(String status, String keyword, int page, int size);
    int count(String keyword);
    Club getById(Long id);
    Club create(Club club);
    Club update(Club club);
    void delete(Long id);
    List<Club> search(String keyword, int page, int size);
    int searchCount(String keyword);
    List<User> getMembers(Long clubId, int page, int size);
    int getMemberCount(Long clubId);
    List<Activity> getActivities(Long clubId, int page, int size);
    int getActivityCount(Long clubId);
}