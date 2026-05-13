package com.club.service.impl;

import com.club.entity.ClubMember;
import com.club.mapper.ClubMemberMapper;
import com.club.service.ClubMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubMemberServiceImpl implements ClubMemberService {

    private final ClubMemberMapper clubMemberMapper;

    @Override
    public List<ClubMember> listByClub(Integer clubId, int page, int size) {
        int offset = (page - 1) * size;
        return clubMemberMapper.findByClubId(clubId, offset, size);
    }

    @Override
    public int countByClub(Integer clubId) {
        return clubMemberMapper.countByClubId(clubId);
    }

    @Override
    public ClubMember addMember(Integer clubId, Integer userId, String role) {
        ClubMember existing = clubMemberMapper.findByClubAndUser(clubId, userId);
        if (existing != null) {
            throw new RuntimeException("该用户已经是社团成员");
        }
        ClubMember member = new ClubMember();
        member.setClubId(clubId);
        member.setUserId(userId);
        member.setRole(role == null || role.isBlank() ? "member" : role);
        member.setStatus("active");
        clubMemberMapper.insert(member);
        return member;
    }

    @Override
    public ClubMember updateMember(Integer clubId, Integer userId, String role, String status) {
        ClubMember member = clubMemberMapper.findByClubAndUser(clubId, userId);
        if (member == null) {
            throw new RuntimeException("社团成员不存在");
        }
        if (role != null && !role.isBlank()) {
            member.setRole(role);
        }
        if (status != null && !status.isBlank()) {
            member.setStatus(status);
        }
        clubMemberMapper.update(member);
        return member;
    }

    @Override
    public void removeMember(Integer clubId, Integer userId) {
        clubMemberMapper.deleteByClubAndUser(clubId, userId);
    }
}
