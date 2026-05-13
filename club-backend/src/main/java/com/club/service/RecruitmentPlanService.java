package com.club.service;

import com.club.entity.RecruitmentPlan;

import java.util.List;

public interface RecruitmentPlanService {
    List<RecruitmentPlan> list(Integer clubId, int page, int size);
    int count(Integer clubId);
    RecruitmentPlan getById(Integer id);
    RecruitmentPlan create(RecruitmentPlan plan);
    RecruitmentPlan update(RecruitmentPlan plan);
    void delete(Integer id);
}
