package com.club.service;

import com.club.entity.ClubMember;

import java.util.List;

public interface ClubMemberService {
    List<ClubMember> listByClub(Integer clubId, int page, int size);
    int countByClub(Integer clubId);
    ClubMember addMember(Integer clubId, Integer userId, String role);
    ClubMember updateMember(Integer clubId, Integer userId, String role, String status);
    void removeMember(Integer clubId, Integer userId);
}
