package com.club.controller;

import com.club.common.SecurityContext;
import com.club.dto.ApprovalActionRequest;
import com.club.entity.Approval;
import com.club.entity.Club;
import com.club.service.ActivityService;
import com.club.service.ApprovalService;
import com.club.service.ClubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovalControllerTest {

    @Mock
    private ApprovalService approvalService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private ClubService clubService;
    @Mock
    private ActivityService activityService;

    private ApprovalController controller;

    @BeforeEach
    void setUp() {
        controller = new ApprovalController(approvalService, securityContext, clubService, activityService);
    }

    @Test
    void teacherCannotHandleSecondStepApproval() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Approval approval = pendingApproval(1, 10, 2, 2);
        Club club = new Club();
        club.setClubId(10);
        club.setAdvisorId(7);

        when(approvalService.getById(1)).thenReturn(approval);
        when(securityContext.isAdmin(request)).thenReturn(false);
        when(securityContext.isTeacher(request)).thenReturn(true);
        when(securityContext.currentUserId(request)).thenReturn(7);
        when(clubService.getById(10L)).thenReturn(club);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.approve(1, new ApprovalActionRequest("同意"), request));

        assertEquals("指导老师只能处理第一步审批", ex.getMessage());
        verify(approvalService, never()).approve(1, 7, "同意");
    }

    @Test
    void adminCannotSkipPendingPreviousStep() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Approval approval = pendingApproval(2, 10, 1, 2);

        when(approvalService.getById(2)).thenReturn(approval);
        when(securityContext.isAdmin(request)).thenReturn(true);
        when(securityContext.isTeacher(request)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.approve(2, new ApprovalActionRequest("同意"), request));

        assertEquals("前置审批未完成，管理员暂不能处理", ex.getMessage());
        verify(approvalService, never()).approve(2, null, "同意");
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
