package com.club.service.impl;

import com.club.entity.*;
import com.club.mapper.*;
import com.club.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final ActivitySignupMapper activitySignupMapper;

    @Override
    public List<Activity> list(String status, Long clubId, int page, int size) {
        int offset = (page - 1) * size;
        if (status != null && !status.isEmpty() && clubId != null) {
            return activityMapper.findByClubIdAndStatus(clubId.intValue(), status, offset, size);
        }
        if (status != null && !status.isEmpty()) {
            return activityMapper.findByStatus(status, offset, size);
        }
        if (clubId != null) {
            return activityMapper.findByClubId(clubId.intValue(), offset, size);
        }
        return activityMapper.findAll(offset, size);
    }

    @Override
    public List<Activity> listManageable(Integer userId, Integer clubId, int page, int size) {
        int offset = (page - 1) * size;
        return activityMapper.findManageable(userId, clubId, offset, size);
    }

    @Override
    public int count(String status, Long clubId) {
        if (status != null && !status.isEmpty() && clubId != null) {
            return activityMapper.countByClubIdAndStatus(clubId.intValue(), status);
        }
        if (status != null && !status.isEmpty()) {
            return activityMapper.countByStatus(status);
        }
        if (clubId != null) {
            return activityMapper.countByClubId(clubId.intValue());
        }
        return activityMapper.countAll();
    }

    @Override
    public int countManageable(Integer userId, Integer clubId) {
        return activityMapper.countManageable(userId, clubId);
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

    @Override
    public List<Activity> searchByStatus(String keyword, String status, int page, int size) {
        int offset = (page - 1) * size;
        return activityMapper.searchByStatus(keyword, status, offset, size);
    }

    @Override
    public int searchCountByStatus(String keyword, String status) {
        return activityMapper.searchCountByStatus(keyword, status);
    }

    @Override
    public List<Activity> searchManageable(String keyword, Integer userId, Integer clubId, int page, int size) {
        int offset = (page - 1) * size;
        return activityMapper.searchManageable(keyword, userId, clubId, offset, size);
    }

    @Override
    public int searchManageableCount(String keyword, Integer userId, Integer clubId) {
        return activityMapper.searchManageableCount(keyword, userId, clubId);
    }

    @Override
    public void signup(Integer activityId, Integer userId) {
        Activity activity = activityMapper.findById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!"approved".equals(activity.getStatus())) {
            throw new RuntimeException("该活动当前不可报名");
        }
        if (activity.getRegistrationDeadline() != null && LocalDateTime.now().isAfter(activity.getRegistrationDeadline())) {
            throw new RuntimeException("活动报名已截止");
        }
        if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0
                && activitySignupMapper.countActiveByActivityId(activityId) >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动报名人数已满");
        }
        if (activitySignupMapper.countByActivityAndUser(activityId, userId) > 0) {
            throw new RuntimeException("您已报名该活动");
        }

        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setUserId(userId);
        activitySignupMapper.insert(signup);
    }

    @Override
    public void cancelSignup(Integer activityId, Integer userId) {
        activitySignupMapper.deleteByActivityAndUser(activityId, userId);
    }

    @Override
    public List<ActivitySignup> getSignups(Integer activityId, int page, int size) {
        int offset = (page - 1) * size;
        return activitySignupMapper.findByActivityId(activityId, offset, size);
    }

    @Override
    public int getSignupCount(Integer activityId) {
        return activitySignupMapper.countByActivityId(activityId);
    }
}
