package com.club.service.impl;

import com.club.common.PageQuery;
import com.club.entity.*;
import com.club.mapper.*;
import com.club.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final ActivitySignupMapper activitySignupMapper;

    @Override
    public List<Activity> list(String status, Long clubId, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        if (status != null && !status.isEmpty() && clubId != null) {
            return activityMapper.findByClubIdAndStatus(clubId.intValue(), status, offset, pageSize);
        }
        if (status != null && !status.isEmpty()) {
            return activityMapper.findByStatus(status, offset, pageSize);
        }
        if (clubId != null) {
            return activityMapper.findByClubId(clubId.intValue(), offset, pageSize);
        }
        return activityMapper.findAll(offset, pageSize);
    }

    @Override
    public List<Activity> listManageable(Integer userId, Integer clubId, boolean includeAdvisor, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return activityMapper.findManageable(userId, clubId, includeAdvisor, offset, pageSize);
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
    public int countManageable(Integer userId, Integer clubId, boolean includeAdvisor) {
        return activityMapper.countManageable(userId, clubId, includeAdvisor);
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
        validate(activity);
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    public Activity update(Activity activity) {
        validate(activity);
        activityMapper.update(activity);
        return activity;
    }

    @Override
    public void delete(Integer id) {
        activityMapper.deleteById(id);
    }

    @Override
    public List<Activity> search(String keyword, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return activityMapper.search(keyword, offset, pageSize);
    }

    @Override
    public int searchCount(String keyword) {
        return activityMapper.searchCount(keyword);
    }

    @Override
    public List<Activity> searchByStatus(String keyword, String status, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return activityMapper.searchByStatus(keyword, status, offset, pageSize);
    }

    @Override
    public int searchCountByStatus(String keyword, String status) {
        return activityMapper.searchCountByStatus(keyword, status);
    }

    @Override
    public List<Activity> searchManageable(String keyword, Integer userId, Integer clubId, boolean includeAdvisor, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return activityMapper.searchManageable(keyword, userId, clubId, includeAdvisor, offset, pageSize);
    }

    @Override
    public int searchManageableCount(String keyword, Integer userId, Integer clubId, boolean includeAdvisor) {
        return activityMapper.searchManageableCount(keyword, userId, clubId, includeAdvisor);
    }

    @Override
    @Transactional
    public void signup(Integer activityId, Integer userId) {
        Activity activity = activityMapper.findByIdForUpdate(activityId);
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
        try {
            activitySignupMapper.insert(signup);
        } catch (DuplicateKeyException e) {
            if (activitySignupMapper.restoreCancelled(activityId, userId) == 0) {
                throw new RuntimeException("您已报名该活动");
            }
        }
    }

    @Override
    public void cancelSignup(Integer activityId, Integer userId) {
        activitySignupMapper.deleteByActivityAndUser(activityId, userId);
    }

    @Override
    public List<ActivitySignup> getSignups(Integer activityId, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return activitySignupMapper.findByActivityId(activityId, offset, pageSize);
    }

    @Override
    public int getSignupCount(Integer activityId) {
        return activitySignupMapper.countByActivityId(activityId);
    }

    private void validate(Activity activity) {
        if (activity.getTitle() == null || activity.getTitle().isBlank()) {
            throw new RuntimeException("活动标题不能为空");
        }
        activity.setTitle(activity.getTitle().trim());
        if (activity.getTitle().length() > 100) {
            throw new RuntimeException("活动标题不能超过100字");
        }
        if (activity.getContent() != null && activity.getContent().length() > 5000) {
            throw new RuntimeException("活动内容不能超过5000字");
        }
        if (activity.getLocation() != null && activity.getLocation().length() > 200) {
            throw new RuntimeException("活动地点不能超过200字");
        }
        if (activity.getContact() != null && activity.getContact().length() > 100) {
            throw new RuntimeException("联系方式不能超过100字");
        }
        if (activity.getMaxParticipants() == null || activity.getMaxParticipants() <= 0) {
            throw new RuntimeException("活动人数上限必须大于0");
        }
        if (activity.getStartTime() == null || activity.getEndTime() == null) {
            throw new RuntimeException("活动开始和结束时间不能为空");
        }
        if (activity.getEndTime().isBefore(activity.getStartTime())) {
            throw new RuntimeException("活动结束时间不能早于开始时间");
        }
        if (activity.getRegistrationDeadline() != null && activity.getRegistrationDeadline().isAfter(activity.getStartTime())) {
            throw new RuntimeException("报名截止时间不能晚于活动开始时间");
        }
    }
}
