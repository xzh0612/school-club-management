package com.club.service.impl;

import com.club.common.PageQuery;
import com.club.entity.RecruitmentPlan;
import com.club.mapper.RecruitmentPlanMapper;
import com.club.service.RecruitmentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecruitmentPlanServiceImpl implements RecruitmentPlanService {

    private final RecruitmentPlanMapper recruitmentPlanMapper;
    private static final Set<String> STATUSES = Set.of("active", "closed", "inactive", "archived");

    @Override
    public List<RecruitmentPlan> list(Integer clubId, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        if (clubId != null) {
            return recruitmentPlanMapper.findByClubId(clubId, offset, pageSize);
        }
        return recruitmentPlanMapper.findAll(offset, pageSize);
    }

    @Override
    public int count(Integer clubId) {
        if (clubId != null) {
            return recruitmentPlanMapper.countByClubId(clubId);
        }
        return recruitmentPlanMapper.countAll();
    }

    @Override
    public RecruitmentPlan getById(Integer id) {
        return recruitmentPlanMapper.findById(id);
    }

    @Override
    public RecruitmentPlan create(RecruitmentPlan plan) {
        if (plan.getStatus() == null || plan.getStatus().isBlank()) {
            plan.setStatus("active");
        }
        validate(plan);
        recruitmentPlanMapper.insert(plan);
        return plan;
    }

    @Override
    public RecruitmentPlan update(RecruitmentPlan plan) {
        validate(plan);
        recruitmentPlanMapper.update(plan);
        return plan;
    }

    @Override
    public void delete(Integer id) {
        recruitmentPlanMapper.deleteById(id);
    }

    private void validate(RecruitmentPlan plan) {
        if (plan.getClubId() == null) {
            throw new RuntimeException("招新计划必须指定所属社团");
        }
        if (plan.getTitle() == null || plan.getTitle().isBlank()) {
            throw new RuntimeException("招新标题不能为空");
        }
        if (plan.getQuota() == null || plan.getQuota() <= 0) {
            throw new RuntimeException("招新人数必须大于0");
        }
        if (plan.getStartDate() != null && plan.getEndDate() != null && plan.getStartDate().isAfter(plan.getEndDate())) {
            throw new RuntimeException("招新开始时间不能晚于截止时间");
        }
        plan.setStatus(plan.getStatus() == null || plan.getStatus().isBlank() ? "active" : plan.getStatus().trim());
        if (!STATUSES.contains(plan.getStatus())) {
            throw new RuntimeException("招新状态不合法：" + plan.getStatus());
        }
        plan.setTitle(plan.getTitle().trim());
    }
}
