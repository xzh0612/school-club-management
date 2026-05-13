package com.club.service.impl;

import com.club.common.PageQuery;
import com.club.entity.*;
import com.club.mapper.ActivityMapper;
import com.club.mapper.ApprovalMapper;
import com.club.mapper.ClubMapper;
import com.club.service.ClubMemberService;
import com.club.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        if (type != null && !type.isBlank() && status != null && !status.isBlank()) {
            return approvalMapper.findByTypeAndStatus(type, status, offset, pageSize);
        }
        if (type != null && !type.isBlank()) {
            return approvalMapper.findByType(type, offset, pageSize);
        }
        if (status != null && !status.isBlank()) {
            return approvalMapper.findByStatus(status, offset, pageSize);
        }
        return approvalMapper.findAll(offset, pageSize);
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
    public List<Approval> listForAdvisor(Integer advisorId, String type, String status, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return approvalMapper.findVisibleToAdvisor(advisorId, type, status, offset, pageSize);
    }

    @Override
    public int countForAdvisor(Integer advisorId, String type, String status) {
        return approvalMapper.countVisibleToAdvisor(advisorId, type, status);
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
        if (approval.getRelatedId() != null
                && approvalMapper.countPendingByTypeAndRelated(approval.getType(), approval.getRelatedId()) > 0) {
            throw new RuntimeException("该事项已有待处理审批，不能重复提交");
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
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return approvalMapper.findPending(offset, pageSize);
    }
    
    @Override
    public int pendingCount() { 
        return approvalMapper.countPending(); 
    }

    @Override
    public List<Approval> pendingListForAdvisor(Integer advisorId, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return approvalMapper.findVisibleToAdvisor(advisorId, null, "pending", offset, pageSize);
    }

    @Override
    public int pendingCountForAdvisor(Integer advisorId) {
        return approvalMapper.countVisibleToAdvisor(advisorId, null, "pending");
    }
    
    @Override
    @Transactional
    public void approve(Integer id, Integer approverId, String comments) {
        Approval existing = approvalMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("审批记录不存在");
        }
        if (!"pending".equals(existing.getStatus())) {
            throw new RuntimeException("审批记录已处理");
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
    @Transactional
    public void reject(Integer id, Integer approverId, String comments) {
        Approval existing = approvalMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("审批记录不存在");
        }
        if (!"pending".equals(existing.getStatus())) {
            throw new RuntimeException("审批记录已处理");
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
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return approvalMapper.search(keyword, offset, pageSize);
    }
    
    @Override
    public int searchCount(String keyword) {
        return approvalMapper.searchCount(keyword);
    }

    @Override
    public List<Approval> searchForAdvisor(Integer advisorId, String keyword, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return approvalMapper.searchVisibleToAdvisor(advisorId, keyword, offset, pageSize);
    }

    @Override
    public int searchCountForAdvisor(Integer advisorId, String keyword) {
        return approvalMapper.searchCountVisibleToAdvisor(advisorId, keyword);
    }
    
    @Override
    public List<Approval> getByApprover(Integer approverId, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return approvalMapper.findByApproverId(approverId, offset, pageSize);
    }
    
    @Override
    public int countByApprover(Integer approverId) {
        return approvalMapper.countByApproverId(approverId);
    }
    
    @Override
    public List<Approval> getByApplicant(Integer applicantId, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        return approvalMapper.findByApplicantId(applicantId, offset, pageSize);
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
