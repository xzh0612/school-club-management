package com.club.service;

import com.club.entity.*;
import java.util.List;

public interface AnnouncementService {
    List<Announcement> list(String status, String targetType, Boolean isTop, int page, int size);
    int count(String status, String targetType, Boolean isTop);
    List<Announcement> listVisible(Integer clubId, String targetType, Boolean isTop, int page, int size);
    int countVisible(Integer clubId, String targetType, Boolean isTop);
    List<Announcement> listManageable(Integer clubId, String status, Boolean isTop, int page, int size);
    int countManageable(Integer clubId, String status, Boolean isTop);
    Announcement getById(Integer id);
    Announcement create(Announcement announcement);
    Announcement update(Announcement announcement);
    void delete(Integer id);
    List<Announcement> search(String keyword, String status, int page, int size);
    int searchCount(String keyword, String status);
    List<Announcement> searchVisible(String keyword, Integer clubId, int page, int size);
    int searchVisibleCount(String keyword, Integer clubId);
    List<Announcement> searchManageable(String keyword, Integer clubId, int page, int size);
    int searchManageableCount(String keyword, Integer clubId);
    void setTop(Integer id, Boolean isTop);
    void publish(Integer id);
    void revoke(Integer id);
}
