package com.club.service.impl;

import com.club.common.PageQuery;
import com.club.entity.*;
import com.club.mapper.AnnouncementMapper;
import com.club.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private static final Set<String> TARGET_TYPES = Set.of("all", "club");
    private static final Set<String> STATUSES = Set.of("draft", "published", "revoked");

    @Override
    public List<Announcement> list(String status, String targetType, Boolean isTop, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return announcementMapper.findByFilters(status, targetType, isTop, offset, pageSize);
    }
    
    @Override
    public int count(String status, String targetType, Boolean isTop) {
        return announcementMapper.countByFilters(status, targetType, isTop);
    }

    @Override
    public List<Announcement> listVisible(List<Integer> clubIds, String targetType, Boolean isTop, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return announcementMapper.findVisible(clubIds, targetType, isTop, offset, pageSize);
    }

    @Override
    public int countVisible(List<Integer> clubIds, String targetType, Boolean isTop) {
        return announcementMapper.countVisible(clubIds, targetType, isTop);
    }

    @Override
    public List<Announcement> listManageable(Integer clubId, String status, Boolean isTop, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return announcementMapper.findManageable(clubId, status, isTop, offset, pageSize);
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
        normalizeAndValidate(announcement);
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
        normalizeAndValidate(announcement);
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
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return announcementMapper.search(keyword, status, offset, pageSize);
    }
    
    @Override
    public int searchCount(String keyword, String status) {
        return announcementMapper.searchCount(keyword, status);
    }

    @Override
    public List<Announcement> searchVisible(String keyword, List<Integer> clubIds, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return announcementMapper.searchVisible(keyword, clubIds, offset, pageSize);
    }

    @Override
    public int searchVisibleCount(String keyword, List<Integer> clubIds) {
        return announcementMapper.searchVisibleCount(keyword, clubIds);
    }

    @Override
    public List<Announcement> searchManageable(String keyword, Integer clubId, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return announcementMapper.searchManageable(keyword, clubId, offset, pageSize);
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

    private void normalizeAndValidate(Announcement announcement) {
        if (announcement.getTitle() == null || announcement.getTitle().isBlank()) {
            throw new RuntimeException("公告标题不能为空");
        }
        if (announcement.getContent() == null || announcement.getContent().isBlank()) {
            throw new RuntimeException("公告内容不能为空");
        }
        if (announcement.getTargetType() == null || announcement.getTargetType().isBlank()) {
            announcement.setTargetType("all");
        } else {
            announcement.setTargetType(announcement.getTargetType().trim());
        }
        if (!TARGET_TYPES.contains(announcement.getTargetType())) {
            throw new RuntimeException("公告发布范围不合法：" + announcement.getTargetType());
        }
        if ("club".equals(announcement.getTargetType()) && announcement.getTargetId() == null) {
            throw new RuntimeException("社团公告必须指定目标社团");
        }
        if ("all".equals(announcement.getTargetType())) {
            announcement.setTargetId(null);
        }
        announcement.setStatus(announcement.getStatus().trim());
        if (!STATUSES.contains(announcement.getStatus())) {
            throw new RuntimeException("公告状态不合法：" + announcement.getStatus());
        }
    }
}
