package com.club.service;

import com.club.entity.*;
import java.util.List;

public interface ApprovalService {
    List<Approval> list(String type, String status, int page, int size);
    int count(String type, String status);
    List<Approval> listForAdvisor(Integer advisorId, String type, String status, int page, int size);
    int countForAdvisor(Integer advisorId, String type, String status);
    Approval getById(Integer id);
    List<ApprovalHistory> histories(Integer approvalId);
    Approval create(Approval approval);
    Approval update(Approval approval);
    void delete(Integer id);
    List<Approval> pendingList(int page, int size);
    int pendingCount();
    List<Approval> pendingListForAdvisor(Integer advisorId, int page, int size);
    int pendingCountForAdvisor(Integer advisorId);
    void approve(Integer id, Integer approverId, String comments);
    void reject(Integer id, Integer approverId, String comments);
    List<Approval> search(String keyword, int page, int size);
    int searchCount(String keyword);
    List<Approval> searchForAdvisor(Integer advisorId, String keyword, int page, int size);
    int searchCountForAdvisor(Integer advisorId, String keyword);
    List<Approval> getByApprover(Integer approverId, int page, int size);
    int countByApprover(Integer approverId);
    List<Approval> getByApplicant(Integer applicantId, int page, int size);
    int countByApplicant(Integer applicantId);
}
