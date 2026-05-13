package com.club.service.impl;

import com.club.common.PageQuery;
import com.club.entity.Application;
import com.club.entity.Club;
import com.club.entity.ClubMember;
import com.club.entity.RecruitmentPlan;
import com.club.mapper.ApplicationMapper;
import com.club.mapper.ClubMemberMapper;
import com.club.mapper.ClubMapper;
import com.club.mapper.RecruitmentPlanMapper;
import com.club.service.ApplicationService;
import com.club.service.ClubMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationMapper applicationMapper;
    private final ClubMapper clubMapper;
    private final RecruitmentPlanMapper recruitmentPlanMapper;
    private final ClubMemberMapper clubMemberMapper;
    private final ClubMemberService clubMemberService;

    @Override
    public List<Application> list(Integer clubId, String status, Integer currentUserId, boolean adminOrTeacher, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        Integer effectiveClubId = resolveClubId(clubId, currentUserId, adminOrTeacher);
        if (effectiveClubId == null) {
            return applicationMapper.findAll(offset, pageSize);
        }
        if (status != null && !status.isBlank()) {
            return applicationMapper.findByClubIdAndStatus(effectiveClubId, status, offset, pageSize);
        }
        return applicationMapper.findByClubId(effectiveClubId, offset, pageSize);
    }

    @Override
    public int count(Integer clubId, String status, Integer currentUserId, boolean adminOrTeacher) {
        Integer effectiveClubId = resolveClubId(clubId, currentUserId, adminOrTeacher);
        if (effectiveClubId == null) {
            return applicationMapper.countAll();
        }
        if (status != null && !status.isBlank()) {
            return applicationMapper.countByClubIdAndStatus(effectiveClubId, status);
        }
        return applicationMapper.countByClubId(effectiveClubId);
    }

    @Override
    public List<Application> listByUser(Integer userId, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return applicationMapper.findByUserId(userId, offset, pageSize);
    }

    @Override
    public int countByUser(Integer userId) {
        return applicationMapper.countByUserId(userId);
    }

    @Override
    public Application getById(Integer id) {
        return applicationMapper.findById(id);
    }

    @Override
    @Transactional
    public Application submit(Integer userId, Integer clubId, Integer recruitmentId, String introduction) {
        if (introduction != null) {
            introduction = introduction.trim();
        }
        if (introduction != null && introduction.length() > 1000) {
            throw new RuntimeException("申请理由不能超过1000字");
        }
        Club club = clubMapper.findById(clubId);
        if (club == null || !"approved".equals(club.getStatus())) {
            throw new RuntimeException("只能申请加入已成立社团");
        }
        ClubMember existingMember = clubMemberMapper.findByClubAndUser(clubId, userId);
        if (existingMember != null && "active".equals(existingMember.getStatus())) {
            throw new RuntimeException("您已经是该社团成员");
        }
        if (applicationMapper.countPendingByUserAndClub(userId, clubId) > 0) {
            throw new RuntimeException("您已提交过该社团的待审核申请");
        }

        RecruitmentPlan recruitment = null;
        if (recruitmentId != null) {
            recruitment = recruitmentPlanMapper.findById(recruitmentId);
            if (recruitment == null || !clubId.equals(recruitment.getClubId())) {
                throw new RuntimeException("招新计划不存在或不属于该社团");
            }
            validateRecruitment(recruitment);
        }

        Application application = new Application();
        application.setUserId(userId);
        application.setClubId(clubId);
        application.setRecruitmentId(recruitmentId);
        application.setIntroduction(introduction);
        application.setStatus("pending");
        applicationMapper.insert(application);
        return application;
    }

    @Override
    @Transactional
    public void approve(Integer id, Integer reviewerId, String comments) {
        Application application = requirePendingApplication(id);
        if (application.getRecruitmentId() != null) {
            RecruitmentPlan recruitment = recruitmentPlanMapper.findByIdForUpdate(application.getRecruitmentId());
            validateRecruitment(recruitment);
        }

        clubMemberService.addMember(application.getClubId(), application.getUserId(), "member");

        Application reviewed = new Application();
        reviewed.setApplicationId(id);
        reviewed.setStatus("approved");
        reviewed.setComments(comments);
        applicationMapper.updateReview(reviewed);
    }

    @Override
    @Transactional
    public void reject(Integer id, Integer reviewerId, String comments) {
        requirePendingApplication(id);
        Application reviewed = new Application();
        reviewed.setApplicationId(id);
        reviewed.setStatus("rejected");
        reviewed.setComments(comments);
        applicationMapper.updateReview(reviewed);
    }

    private Application requirePendingApplication(Integer id) {
        Application application = applicationMapper.findByIdForUpdate(id);
        if (application == null) {
            throw new RuntimeException("入社申请不存在");
        }
        if (!"pending".equals(application.getStatus())) {
            throw new RuntimeException("该入社申请已处理");
        }
        return application;
    }

    private Integer resolveClubId(Integer clubId, Integer currentUserId, boolean adminOrTeacher) {
        if (clubId != null) {
            return clubId;
        }
        if (!adminOrTeacher) {
            Club managedClub = clubMapper.findManageable(currentUserId, null, false, 0, 1).stream().findFirst().orElse(null);
            if (managedClub == null) {
                throw new RuntimeException("当前用户未绑定可管理社团");
            }
            return managedClub.getClubId();
        }
        return null;
    }

    private void validateRecruitment(RecruitmentPlan recruitment) {
        if (recruitment == null) {
            throw new RuntimeException("招新计划不存在");
        }
        if (!"active".equals(recruitment.getStatus())) {
            throw new RuntimeException("招新计划当前不可申请");
        }
        LocalDate today = LocalDate.now();
        if (recruitment.getStartDate() != null && today.isBefore(recruitment.getStartDate())) {
            throw new RuntimeException("招新尚未开始");
        }
        if (recruitment.getEndDate() != null && today.isAfter(recruitment.getEndDate())) {
            throw new RuntimeException("招新已结束");
        }
        if (recruitment.getQuota() != null && recruitment.getQuota() > 0
                && applicationMapper.countApprovedByRecruitmentId(recruitment.getRecruitmentId()) >= recruitment.getQuota()) {
            throw new RuntimeException("招新名额已满");
        }
    }
}
