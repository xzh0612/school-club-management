package com.club.service.impl;

import com.club.entity.*;
import com.club.mapper.*;
import com.club.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;

    @Override
    public List<Activity> list(String status, Long clubId, int page, int size) {
        int offset = (page - 1) * size;
        if (status != null && !status.isEmpty()) {
            return activityMapper.findByStatus(status, offset, size);
        }
        if (clubId != null) {
            return activityMapper.findByClubId(clubId.intValue(), offset, size);
        }
        return activityMapper.findAll(offset, size);
    }

    @Override
    public int count(String status, Long clubId) {
        if (status != null && !status.isEmpty()) {
            return activityMapper.countByStatus(status);
        }
        if (clubId != null) {
            return activityMapper.countByClubId(clubId.intValue());
        }
        return activityMapper.countAll();
    }

    @Override
    public Activity getById(Integer id) {
        return activityMapper.findById(id);
    }

    @Override
    public List<Activity> byClubId(Long clubId) {
        return activityMapper.findByClubIdSimple(clubId.intValue());
    }

    @Override
    public Activity create(Activity activity) {
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    public Activity update(Activity activity) {
        activityMapper.update(activity);
        return activity;
    }

    @Override
    public void delete(Integer id) {
        activityMapper.deleteById(id);
    }

    @Override
    public List<Activity> search(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        return activityMapper.search(keyword, offset, size);
    }

    @Override
    public int searchCount(String keyword) {
        return activityMapper.searchCount(keyword);
    }
}