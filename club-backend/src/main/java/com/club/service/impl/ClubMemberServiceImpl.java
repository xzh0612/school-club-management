package com.club.service.impl;

import com.club.common.PageQuery;
import com.club.entity.ClubMember;
import com.club.mapper.ClubMemberMapper;
import com.club.service.ClubMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClubMemberServiceImpl implements ClubMemberService {

    private final ClubMemberMapper clubMemberMapper;
    private static final Set<String> MEMBER_ROLES = Set.of("leader", "club_leader", "deputy_leader", "member");
    private static final Set<String> MEMBER_STATUSES = Set.of("active", "inactive", "quit");

    @Override
    public List<ClubMember> listByClub(Integer clubId, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return clubMemberMapper.findByClubId(clubId, offset, pageSize);
    }

    @Override
    public int countByClub(Integer clubId) {
        return clubMemberMapper.countByClubId(clubId);
    }

    @Override
    public List<Integer> listActiveClubIdsByUser(Integer userId) {
        return clubMemberMapper.findActiveClubIdsByUser(userId);
    }

    @Override
    public List<Integer> listLeaderClubIdsByUser(Integer userId) {
        return clubMemberMapper.findActiveLeaderClubIdsByUser(userId);
    }

    @Override
    public boolean isLeaderOfClub(Integer clubId, Integer userId) {
        return clubId != null && userId != null && clubMemberMapper.countActiveLeaderMembership(clubId, userId) > 0;
    }

    @Override
    @Transactional
    public ClubMember addMember(Integer clubId, Integer userId, String role) {
        String effectiveRole = normalizeRole(role);
        ClubMember existing = clubMemberMapper.findByClubAndUser(clubId, userId);
        if (existing != null) {
            if ("active".equals(existing.getStatus())) {
                throw new RuntimeException("该用户已经是社团成员");
            }
            existing.setRole(effectiveRole);
            existing.setStatus("active");
            clubMemberMapper.update(existing);
            return existing;
        }
        ClubMember member = new ClubMember();
        member.setClubId(clubId);
        member.setUserId(userId);
        member.setRole(effectiveRole);
        member.setStatus("active");
        clubMemberMapper.insert(member);
        return member;
    }

    @Override
    @Transactional
    public ClubMember updateMember(Integer clubId, Integer userId, String role, String status) {
        ClubMember member = clubMemberMapper.findByClubAndUser(clubId, userId);
        if (member == null) {
            throw new RuntimeException("社团成员不存在");
        }
        String nextRole = role != null && !role.isBlank() ? normalizeRole(role) : member.getRole();
        String nextStatus = status != null && !status.isBlank() ? normalizeStatus(status) : member.getStatus();
        ensureLeaderRemains(member, nextRole, nextStatus);
        if (role != null && !role.isBlank()) {
            member.setRole(nextRole);
        }
        if (status != null && !status.isBlank()) {
            member.setStatus(nextStatus);
        }
        clubMemberMapper.update(member);
        return member;
    }

    @Override
    @Transactional
    public void removeMember(Integer clubId, Integer userId) {
        ClubMember member = clubMemberMapper.findByClubAndUser(clubId, userId);
        if (member == null) {
            return;
        }
        ensureLeaderRemains(member, null, "quit");
        clubMemberMapper.deleteByClubAndUser(clubId, userId);
    }

    private String normalizeRole(String role) {
        String value = role == null || role.isBlank() ? "member" : role.trim();
        if (!MEMBER_ROLES.contains(value)) {
            throw new RuntimeException("成员角色不合法：" + value);
        }
        return value;
    }

    private String normalizeStatus(String status) {
        String value = status.trim();
        if (!MEMBER_STATUSES.contains(value)) {
            throw new RuntimeException("成员状态不合法：" + value);
        }
        return value;
    }

    private void ensureLeaderRemains(ClubMember current, String nextRole, String nextStatus) {
        boolean currentIsActiveLeader = isLeaderRole(current.getRole()) && "active".equals(current.getStatus());
        boolean nextIsActiveLeader = isLeaderRole(nextRole) && "active".equals(nextStatus);
        if (currentIsActiveLeader && !nextIsActiveLeader
                && clubMemberMapper.countActiveLeadersByClub(current.getClubId()) <= 1) {
            throw new RuntimeException("至少保留一名正常状态的社团负责人");
        }
    }

    private boolean isLeaderRole(String role) {
        return "leader".equals(role) || "club_leader".equals(role);
    }
}
