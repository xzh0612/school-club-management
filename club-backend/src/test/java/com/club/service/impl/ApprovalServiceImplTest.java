package com.club.service.impl;

import com.club.entity.Approval;
import com.club.entity.ApprovalHistory;
import com.club.entity.Club;
import com.club.mapper.ActivityChangeRequestMapper;
import com.club.mapper.ActivityMapper;
import com.club.mapper.ApprovalHistoryMapper;
import com.club.mapper.ApprovalMapper;
import com.club.mapper.ClubMapper;
import com.club.service.ClubMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceImplTest {

    @Mock
    private ApprovalMapper approvalMapper;
    @Mock
    private ClubMapper clubMapper;
    @Mock
    private ActivityMapper activityMapper;
    @Mock
    private ActivityChangeRequestMapper activityChangeRequestMapper;
    @Mock
    private ApprovalHistoryMapper approvalHistoryMapper;
    @Mock
    private ClubMemberService clubMemberService;

    private ApprovalServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ApprovalServiceImpl(
                approvalMapper,
                clubMapper,
                activityMapper,
                activityChangeRequestMapper,
                approvalHistoryMapper,
                clubMemberService
        );
    }

    @Test
    void approveIntermediateStepOnlyAdvancesProgress() {
        Approval approval = pendingApproval(1, 10, 1, 2);
        when(approvalMapper.findById(1)).thenReturn(approval);

        service.approve(1, 99, "初审通过");

        ArgumentCaptor<Approval> stepCaptor = ArgumentCaptor.forClass(Approval.class);
        verify(approvalMapper).updateStep(stepCaptor.capture());
        Approval updated = stepCaptor.getValue();
        assertEquals(1, updated.getApprovalId());
        assertEquals(99, updated.getApproverId());
        assertEquals(2, updated.getCurrentStep());
        assertEquals("初审通过", updated.getComments());

        verify(approvalMapper, never()).updateStatus(any());
        verify(clubMapper, never()).update(any());
        verify(activityMapper, never()).update(any());

        ArgumentCaptor<ApprovalHistory> historyCaptor = ArgumentCaptor.forClass(ApprovalHistory.class);
        verify(approvalHistoryMapper).insert(historyCaptor.capture());
        ApprovalHistory history = historyCaptor.getValue();
        assertEquals(1, history.getApprovalId());
        assertEquals(1, history.getStepNo());
        assertEquals(99, history.getOperatorId());
        assertEquals("advanced", history.getAction());
        assertEquals("初审通过", history.getComments());
    }

    @Test
    void approveFinalStepAppliesClubStatus() {
        Approval approval = pendingApproval(2, 10, 2, 2);
        Club club = new Club();
        club.setClubId(10);
        club.setStatus("pending");
        when(approvalMapper.findById(2)).thenReturn(approval);
        when(clubMapper.findById(10)).thenReturn(club);

        service.approve(2, 100, "终审通过");

        ArgumentCaptor<Approval> statusCaptor = ArgumentCaptor.forClass(Approval.class);
        verify(approvalMapper).updateStatus(statusCaptor.capture());
        Approval updated = statusCaptor.getValue();
        assertEquals(2, updated.getApprovalId());
        assertEquals(100, updated.getApproverId());
        assertEquals("approved", updated.getStatus());
        assertEquals("终审通过", updated.getComments());

        ArgumentCaptor<Club> clubCaptor = ArgumentCaptor.forClass(Club.class);
        verify(clubMapper).update(clubCaptor.capture());
        Club updatedClub = clubCaptor.getValue();
        assertNotNull(updatedClub);
        assertEquals(10, updatedClub.getClubId());
        assertEquals("approved", updatedClub.getStatus());

        ArgumentCaptor<ApprovalHistory> historyCaptor = ArgumentCaptor.forClass(ApprovalHistory.class);
        verify(approvalHistoryMapper).insert(historyCaptor.capture());
        ApprovalHistory history = historyCaptor.getValue();
        assertEquals(2, history.getApprovalId());
        assertEquals(2, history.getStepNo());
        assertEquals(100, history.getOperatorId());
        assertEquals("approved", history.getAction());
        assertEquals("终审通过", history.getComments());
    }

    private Approval pendingApproval(int approvalId, int relatedId, int currentStep, int totalSteps) {
        Approval approval = new Approval();
        approval.setApprovalId(approvalId);
        approval.setType("club_creation");
        approval.setRelatedId(relatedId);
        approval.setStatus("pending");
        approval.setCurrentStep(currentStep);
        approval.setTotalSteps(totalSteps);
        return approval;
    }
}
