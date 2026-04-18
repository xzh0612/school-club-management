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
    public List<Announcement> list(String targetType, Boolean isTop, int page, int size) {
        int offset = (page - 1) * size;
        return announcementMapper.findAll(offset, size);
    }
    
    @Override
    public int count(String targetType, Boolean isTop) { 
        return announcementMapper.countAll(); 
    }
    
    @Override
    public Announcement getById(Integer id) {
        return announcementMapper.findById(id);
    }
    
    @Override
    public Announcement create(Announcement announcement) { 
        announcementMapper.insert(announcement);
        return announcement;
    }
    
    @Override
    public Announcement update(Announcement announcement) {
        announcementMapper.update(announcement);
        return announcement;
    }
    
    @Override
    public void delete(Integer id) {
        announcementMapper.deleteById(id);
    }
    
    @Override
    public List<Announcement> search(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        return announcementMapper.search(keyword, offset, size);
    }
    
    @Override
    public int searchCount(String keyword) {
        return announcementMapper.searchCount(keyword);
    }
    
    @Override
    public void setTop(Integer id, Boolean isTop) {
        // TODO: 实现置顶功能
    }
    
    @Override
    public void publish(Integer id) {
        // TODO: 实现发布功能
    }
    
    @Override
    public void revoke(Integer id) {
        // TODO: 实现撤销功能
    }
}