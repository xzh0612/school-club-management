package com.club.mapper;

import com.club.entity.ActivityChangeRequest;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ActivityChangeRequestMapper {

    @Select("SELECT * FROM activity_change_requests WHERE activity_id = #{activityId} AND status = 'pending' ORDER BY create_time DESC, change_id DESC LIMIT 1")
    ActivityChangeRequest findPendingByActivityId(Integer activityId);

    @Insert("INSERT INTO activity_change_requests(activity_id, requester_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status) " +
            "VALUES(#{activityId}, #{requesterId}, #{title}, #{content}, #{type}, #{maxParticipants}, #{registrationDeadline}, #{organizer}, #{contact}, #{startTime}, #{endTime}, #{location}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "changeId")
    int insert(ActivityChangeRequest changeRequest);

    @Update("UPDATE activity_change_requests SET status = #{status}, update_time = NOW() WHERE change_id = #{changeId}")
    int updateStatus(@Param("changeId") Integer changeId, @Param("status") String status);
}
