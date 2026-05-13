package com.club.service.impl;

import com.club.entity.*;
import com.club.mapper.ActivityMapper;
import com.club.mapper.ApprovalMapper;
import com.club.mapper.ClubMapper;
import com.club.service.ClubMemberService;
import com.club.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalMapper approvalMapper;
    private final ClubMapper clubMapper;
    private final ActivityMapper activityMapper;
    private final ClubMemberService clubMemberService;

    @Override
    public List<Approval> list(String type, String status, int page, int size) {
        int offset = (page - 1) * size;
        if (type != null && !type.isBlank() && status != null && !status.isBlank()) {
            return approvalMapper.findByTypeAndStatus(type, status, offset, size);
        }
        if (type != null && !type.isBlank()) {
            return approvalMapper.findByType(type, offset, size);
        }
        if (status != null && !status.isBlank()) {
            return approvalMapper.findByStatus(status, offset, size);
        }
        return approvalMapper.findAll(offset, size);
    }
    
    @Override
    public int count(String type, String status) {
        if (type != null && !type.isBlank() && status != null && !status.isBlank()) {
            return approvalMapper.countByTypeAndStatus(type, status);
        }
        if (type != null && !type.isBlank()) {
            return approvalMapper.countByType(type);
        }
        if (status != null && !status.isBlank()) {
            return approvalMapper.countByStatus(status);
        }
        return approvalMapper.countAll();
    }
    
    @Override
    public Approval getById(Integer id) {
        return approvalMapper.findById(id);
    }
    
    @Override
    public Approval create(Approval approval) {
        if (!"club_creation".equals(approval.getType()) && !"activity_application".equals(approval.getType())) {
            throw new RuntimeException("审批类型只能是社团成立或活动申请");
        }
        if (approval.getCurrentStep() == null) {
            approval.setCurrentStep(1);
        }
        if (approval.getTotalSteps() == null) {
            approval.setTotalSteps(1);
        }
        approvalMapper.insert(approval);
        return approval;
    }
    
    @Override
    public Approval update(Approval approval) {
        approvalMapper.updateStatus(approval);
        return approval;
    }
    
    @Override
    public void delete(Integer id) {
        approvalMapper.deleteById(id);
    }
    
    @Override
    public List<Approval> pendingList(int page, int size) {
        int offset = (page - 1) * size;
        return approvalMapper.findPending(offset, size);
    }
    
    @Override
    public int pendingCount() { 
        return approvalMapper.countPending(); 
    }
    
    @Override
    public void approve(Integer id, Integer approverId, String comments) {
        Approval existing = approvalMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("审批记录不存在");
        }
        if ("recruitment_application".equals(existing.getType())) {
            throw new RuntimeException("入社申请应由社团负责人在招新管理中审核");
        }
        Approval approval = new Approval();
        approval.setApprovalId(id);
        approval.setApproverId(approverId);
        approval.setStatus("approved");
        approval.setComments(comments);
        approvalMapper.updateStatus(approval);
        applyApprovalResult(existing, "approved");
    }
    
    @Override
    public void reject(Integer id, Integer approverId, String comments) {
        Approval existing = approvalMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("审批记录不存在");
        }
        if ("recruitment_application".equals(existing.getType())) {
            throw new RuntimeException("入社申请应由社团负责人在招新管理中审核");
        }
        Approval approval = new Approval();
        approval.setApprovalId(id);
        approval.setApproverId(approverId);
        approval.setStatus("rejected");
        approval.setComments(comments);
        approvalMapper.updateStatus(approval);
        applyApprovalResult(existing, "rejected");
    }
    
    @Override
    public List<Approval> search(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        return approvalMapper.search(keyword, offset, size);
    }
    
    @Override
    public int searchCount(String keyword) {
        return approvalMapper.searchCount(keyword);
    }
    
    @Override
    public List<Approval> getByApprover(Integer approverId, int page, int size) {
        int offset = (page - 1) * size;
        return approvalMapper.findByApproverId(approverId, offset, size);
    }
    
    @Override
    public int countByApprover(Integer approverId) {
        return approvalMapper.countByApproverId(approverId);
    }
    
    @Override
    public List<Approval> getByApplicant(Integer applicantId, int page, int size) {
        int offset = (page - 1) * size;
        return approvalMapper.findByApplicantId(applicantId, offset, size);
    }
    
    @Override
    public int countByApplicant(Integer applicantId) {
        return approvalMapper.countByApplicantId(applicantId);
    }
    
    private void applyApprovalResult(Approval approval, String status) {
        if (approval.getRelatedId() == null) {
            return;
        }
        switch (approval.getType()) {
            case "club_creation" -> updateClubStatus(approval.getRelatedId(), status);
            case "activity_application" -> updateActivityStatus(approval.getRelatedId(), status);
            default -> {
            }
        }
    }

    private void updateClubStatus(Integer clubId, String status) {
        Club club = clubMapper.findById(clubId);
        if (club == null) {
            return;
        }
        club.setStatus(status);
        clubMapper.update(club);
    }

    private void updateActivityStatus(Integer activityId, String status) {
        Activity activity = activityMapper.findById(activityId);
        if (activity == null) {
            return;
        }
        activity.setStatus(status);
        activityMapper.update(activity);
    }
}
