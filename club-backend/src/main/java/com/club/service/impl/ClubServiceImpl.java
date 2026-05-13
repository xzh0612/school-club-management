package com.club.service.impl;

import com.club.common.PageQuery;
import com.club.entity.*;
import com.club.mapper.*;
import com.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubMapper clubMapper;
    private final ActivityMapper activityMapper;
    private final UserMapper userMapper;

    @Override
    public List<Club> list(String status, String keyword, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        if (keyword != null && !keyword.isEmpty()) {
            if (status != null && !status.isEmpty()) {
                return clubMapper.searchByStatus(keyword, status, offset, pageSize);
            }
            return clubMapper.search(keyword, offset, pageSize);
        }
        if (status != null && !status.isEmpty()) {
            return clubMapper.findByStatus(status, offset, pageSize);
        }
        return clubMapper.findAll(offset, pageSize);
    }

    @Override
    public List<Club> listManageable(Integer userId, Integer clubId, boolean includeAdvisor, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return clubMapper.findManageable(userId, clubId, includeAdvisor, offset, pageSize);
    }

    @Override
    public int count(String status, String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            if (status != null && !status.isEmpty()) {
                return clubMapper.searchCountByStatus(keyword, status);
            }
            return clubMapper.searchCount(keyword);
        }
        if (status != null && !status.isEmpty()) {
            return clubMapper.countByStatus(status);
        }
        return clubMapper.countAll();
    }

    @Override
    public int countManageable(Integer userId, Integer clubId, boolean includeAdvisor) {
        return clubMapper.countManageable(userId, clubId, includeAdvisor);
    }

    @Override
    public java.util.Map<String, Integer> stats(Integer userId, Integer clubId, boolean includeAdvisor, boolean global) {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        if (global) {
            stats.put("total", clubMapper.countAll());
            stats.put("approved", clubMapper.countByStatus("approved"));
            stats.put("pending", clubMapper.countByStatus("pending"));
            stats.put("classified", clubMapper.countClassified());
            return stats;
        }
        stats.put("total", clubMapper.countManageable(userId, clubId, includeAdvisor));
        stats.put("approved", clubMapper.countManageableByStatus(userId, clubId, includeAdvisor, "approved"));
        stats.put("pending", clubMapper.countManageableByStatus(userId, clubId, includeAdvisor, "pending"));
        stats.put("classified", clubMapper.countManageableClassified(userId, clubId, includeAdvisor));
        return stats;
    }

    @Override
    public Club getById(Long id) {
        return clubMapper.findById(id.intValue());
    }

    @Override
    public Club create(Club club) {
        if (club.getClubType() == null || club.getClubType().isBlank()) {
            club.setClubType("general");
        }
        clubMapper.insert(club);
        return club;
    }

    @Override
    public Club update(Club club) {
        clubMapper.update(club);
        return club;
    }

    @Override
    public void delete(Long id) {
        clubMapper.deleteById(id.intValue());
    }

    @Override
    public List<Club> search(String keyword, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return clubMapper.search(keyword, offset, pageSize);
    }

    @Override
    public int searchCount(String keyword) {
        return clubMapper.searchCount(keyword);
    }

    @Override
    public List<Club> searchByStatus(String keyword, String status, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return clubMapper.searchByStatus(keyword, status, offset, pageSize);
    }

    @Override
    public int searchCountByStatus(String keyword, String status) {
        return clubMapper.searchCountByStatus(keyword, status);
    }

    @Override
    public List<Club> searchManageable(String keyword, Integer userId, Integer clubId, boolean includeAdvisor, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return clubMapper.searchManageable(keyword, userId, clubId, includeAdvisor, offset, pageSize);
    }

    @Override
    public int searchManageableCount(String keyword, Integer userId, Integer clubId, boolean includeAdvisor) {
        return clubMapper.searchManageableCount(keyword, userId, clubId, includeAdvisor);
    }

    @Override
    public List<User> getMembers(Long clubId, int page, int size) {
        // 社团成员查询由 UserService.listByClub 承担，保留接口避免破坏旧调用。
        return List.of();
    }

    @Override
    public int getMemberCount(Long clubId) {
        if (clubId == null) {
            return 0;
        }
        return userMapper.countByClubId(clubId.intValue());
    }

    @Override
    public List<Activity> getActivities(Long clubId, String status, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        if (status != null && !status.isEmpty()) {
            return activityMapper.findByClubIdAndStatus(clubId.intValue(), status, offset, pageSize);
        }
        return activityMapper.findByClubId(clubId.intValue(), offset, pageSize);
    }

    @Override
    public int getActivityCount(Long clubId, String status) {
        if (status != null && !status.isEmpty()) {
            return activityMapper.countByClubIdAndStatus(clubId.intValue(), status);
        }
        return activityMapper.countByClubId(clubId.intValue());
    }
}
