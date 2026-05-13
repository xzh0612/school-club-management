package com.club.service.impl;

import com.club.entity.RecruitmentPlan;
import com.club.mapper.RecruitmentPlanMapper;
import com.club.service.RecruitmentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitmentPlanServiceImpl implements RecruitmentPlanService {

    private final RecruitmentPlanMapper recruitmentPlanMapper;

    @Override
    public List<RecruitmentPlan> list(Integer clubId, int page, int size) {
        int offset = (page - 1) * size;
        if (clubId != null) {
            return recruitmentPlanMapper.findByClubId(clubId, offset, size);
        }
        return recruitmentPlanMapper.findAll(offset, size);
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
        recruitmentPlanMapper.insert(plan);
        return plan;
    }

    @Override
    public RecruitmentPlan update(RecruitmentPlan plan) {
        recruitmentPlanMapper.update(plan);
        return plan;
    }

    @Override
    public void delete(Integer id) {
        recruitmentPlanMapper.deleteById(id);
    }
}
