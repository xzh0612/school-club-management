package com.club.service.impl;

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

    @Override
    public List<Club> list(String status, String keyword, int page, int size) {
        int offset = (page - 1) * size;
        if (keyword != null && !keyword.isEmpty()) {
            return clubMapper.search(keyword, offset, size);
        }
        if (status != null && !status.isEmpty()) {
            return clubMapper.findByStatus(status, offset, size);
        }
        return clubMapper.findAll(offset, size);
    }

    @Override
    public int count(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return clubMapper.searchCount(keyword);
        }
        return clubMapper.countAll();
    }

    @Override
    public Club getById(Long id) {
        return clubMapper.findById(id.intValue());
    }

    @Override
    public Club create(Club club) {
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
        int offset = (page - 1) * size;
        return clubMapper.search(keyword, offset, size);
    }

    @Override
    public int searchCount(String keyword) {
        return clubMapper.searchCount(keyword);
    }

    @Override
    public List<User> getMembers(Long clubId, int page, int size) {
        // TODO: 实现获取社团成员
        return List.of();
    }

    @Override
    public int getMemberCount(Long clubId) {
        // TODO: 实现获取社团成员数量
        return 0;
    }

    @Override
    public List<Activity> getActivities(Long clubId, int page, int size) {
        // TODO: 实现获取社团活动
        return List.of();
    }

    @Override
    public int getActivityCount(Long clubId) {
        // TODO: 实现获取社团活动数量
        return 0;
    }
}