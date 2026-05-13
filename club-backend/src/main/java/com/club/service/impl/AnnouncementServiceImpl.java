package com.club.service.impl;

import com.club.entity.*;
import com.club.mapper.AnnouncementMapper;
import com.club.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> list(String status, String targetType, Boolean isTop, int page, int size) {
        int offset = (page - 1) * size;
        return announcementMapper.findByFilters(status, targetType, isTop, offset, size);
    }
    
    @Override
    public int count(String status, String targetType, Boolean isTop) {
        return announcementMapper.countByFilters(status, targetType, isTop);
    }

    @Override
    public List<Announcement> listVisible(Integer clubId, String targetType, Boolean isTop, int page, int size) {
        int offset = (page - 1) * size;
        return announcementMapper.findVisible(clubId, targetType, isTop, offset, size);
    }

    @Override
    public int countVisible(Integer clubId, String targetType, Boolean isTop) {
        return announcementMapper.countVisible(clubId, targetType, isTop);
    }

    @Override
    public List<Announcement> listManageable(Integer clubId, String status, Boolean isTop, int page, int size) {
        int offset = (page - 1) * size;
        return announcementMapper.findManageable(clubId, status, isTop, offset, size);
    }

    @Override
    public int countManageable(Integer clubId, String status, Boolean isTop) {
        return announcementMapper.countManageable(clubId, status, isTop);
    }
    
    @Override
    public Announcement getById(Integer id) {
        return announcementMapper.findById(id);
    }
    
    @Override
    public Announcement create(Announcement announcement) { 
        if (announcement.getStatus() == null || announcement.getStatus().isBlank()) {
            announcement.setStatus("published");
        }
        if (announcement.getIsTop() == null) {
            announcement.setIsTop(0);
        }
        announcementMapper.insert(announcement);
        return announcement;
    }
    
    @Override
    public Announcement update(Announcement announcement) {
        Announcement existing = announcementMapper.findById(announcement.getAnnouncementId());
        if (existing == null) throw new RuntimeException("公告不存在");
        if (announcement.getStatus() == null || announcement.getStatus().isBlank()) {
            announcement.setStatus(existing.getStatus());
        }
        if (announcement.getIsTop() == null) {
            announcement.setIsTop(existing.getIsTop());
        }
        announcementMapper.update(announcement);
        return announcement;
    }
    
    @Override
    public void delete(Integer id) {
        announcementMapper.deleteById(id);
    }
    
    @Override
    public List<Announcement> search(String keyword, String status, int page, int size) {
        int offset = (page - 1) * size;
        return announcementMapper.search(keyword, status, offset, size);
    }
    
    @Override
    public int searchCount(String keyword, String status) {
        return announcementMapper.searchCount(keyword, status);
    }

    @Override
    public List<Announcement> searchVisible(String keyword, Integer clubId, int page, int size) {
        int offset = (page - 1) * size;
        return announcementMapper.searchVisible(keyword, clubId, offset, size);
    }

    @Override
    public int searchVisibleCount(String keyword, Integer clubId) {
        return announcementMapper.searchVisibleCount(keyword, clubId);
    }

    @Override
    public List<Announcement> searchManageable(String keyword, Integer clubId, int page, int size) {
        int offset = (page - 1) * size;
        return announcementMapper.searchManageable(keyword, clubId, offset, size);
    }

    @Override
    public int searchManageableCount(String keyword, Integer clubId) {
        return announcementMapper.searchManageableCount(keyword, clubId);
    }
    
    @Override
    public void setTop(Integer id, Boolean isTop) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) throw new RuntimeException("公告不存在");
        announcement.setIsTop(Boolean.TRUE.equals(isTop) ? 1 : 0);
        announcementMapper.updateTopStatus(announcement);
    }
    
    @Override
    public void publish(Integer id) {
        updateStatus(id, "published");
    }
    
    @Override
    public void revoke(Integer id) {
        updateStatus(id, "revoked");
    }

    private void updateStatus(Integer id, String status) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) throw new RuntimeException("公告不存在");
        announcement.setStatus(status);
        announcementMapper.updateStatus(announcement);
    }
}
