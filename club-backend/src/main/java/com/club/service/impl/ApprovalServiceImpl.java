package com.club.service.impl;

import com.club.entity.*;
import com.club.mapper.ApprovalMapper;
import com.club.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalMapper approvalMapper;

    @Override
    public List<Approval> list(String type, String status, int page, int size) {
        int offset = (page - 1) * size;
        return approvalMapper.findAll(offset, size);
    }
    
    @Override
    public int count(String type, String status) {
        return approvalMapper.countAll();
    }
    
    @Override
    public Approval getById(Integer id) {
        return approvalMapper.findById(id);
    }
    
    @Override
    public Approval create(Approval approval) {
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
    public void approve(Integer id, String comments) {
        Approval approval = new Approval();
        approval.setApprovalId(id);
        approval.setStatus("approved");
        approval.setComments(comments);
        approvalMapper.updateStatus(approval);
    }
    
    @Override
    public void reject(Integer id, String comments) {
        Approval approval = new Approval();
        approval.setApprovalId(id);
        approval.setStatus("rejected");
        approval.setComments(comments);
        approvalMapper.updateStatus(approval);
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
    
    @Override
    public Approval submitRecruitmentApplication(Integer userId, Integer clubId, String introduction) {
        Approval approval = new Approval();
        approval.setType("recruitment_application");
        approval.setRelatedId(clubId);
        approval.setApplicantId(userId);
        approval.setStatus("pending");
        approval.setComments(introduction);
        return create(approval);
    }
    
    @Override
    public int countPendingRecruitmentApplications() {
        return approvalMapper.countByTypeAndStatus("recruitment_application", "pending");
    }
}