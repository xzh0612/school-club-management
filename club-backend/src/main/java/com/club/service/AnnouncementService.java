package com.club.service;

import com.club.entity.*;
import java.util.List;

public interface AnnouncementService {
    List<Announcement> list(String targetType, Boolean isTop, int page, int size);
    int count(String targetType, Boolean isTop);
    Announcement getById(Integer id);
    Announcement create(Announcement announcement);
    Announcement update(Announcement announcement);
    void delete(Integer id);
    List<Announcement> search(String keyword, int page, int size);
    int searchCount(String keyword);
    void setTop(Integer id, Boolean isTop);
    void publish(Integer id);
    void revoke(Integer id);
}