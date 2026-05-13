package com.club.service.impl;

import com.club.entity.Activity;
import com.club.mapper.ActivityMapper;
import com.club.mapper.ActivitySignupMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ActivityServiceImplTest {

    private final ActivityServiceImpl service = new ActivityServiceImpl(
            mock(ActivityMapper.class),
            mock(ActivitySignupMapper.class)
    );

    @Test
    void rejectsBlankTitle() {
        Activity activity = validActivity();
        activity.setTitle(" ");

        assertThrows(RuntimeException.class, () -> service.create(activity));
    }

    @Test
    void rejectsInvalidTimeRange() {
        Activity activity = validActivity();
        activity.setEndTime(activity.getStartTime().minusHours(1));

        assertThrows(RuntimeException.class, () -> service.create(activity));
    }

    @Test
    void rejectsDeadlineAfterStart() {
        Activity activity = validActivity();
        activity.setRegistrationDeadline(activity.getStartTime().plusMinutes(1));

        assertThrows(RuntimeException.class, () -> service.create(activity));
    }

    private Activity validActivity() {
        Activity activity = new Activity();
        activity.setClubId(1);
        activity.setTitle("读书分享会");
        activity.setContent("分享近期阅读内容");
        activity.setMaxParticipants(20);
        activity.setRegistrationDeadline(LocalDateTime.now().plusDays(1));
        activity.setStartTime(LocalDateTime.now().plusDays(2));
        activity.setEndTime(LocalDateTime.now().plusDays(2).plusHours(2));
        activity.setLocation("教学楼A101");
        return activity;
    }
}
