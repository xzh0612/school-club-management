package com.club.mapper;

import com.club.entity.ActivitySignup;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivitySignupMapper {

    @Select("SELECT COUNT(*) FROM activity_signups WHERE activity_id = #{activityId} AND user_id = #{userId}")
    int countByActivityAndUser(@Param("activityId") Integer activityId, @Param("userId") Integer userId);

    @Insert("INSERT INTO activity_signups(activity_id, user_id, status) VALUES(#{activityId}, #{userId}, 'pending')")
    @Options(useGeneratedKeys = true, keyProperty = "signupId")
    int insert(ActivitySignup signup);

    @Delete("DELETE FROM activity_signups WHERE activity_id = #{activityId} AND user_id = #{userId}")
    int deleteByActivityAndUser(@Param("activityId") Integer activityId, @Param("userId") Integer userId);

    @Select("SELECT s.*, a.title AS activity_title, u.real_name AS user_name, u.student_id AS user_sid " +
            "FROM activity_signups s " +
            "LEFT JOIN activities a ON s.activity_id = a.activity_id " +
            "LEFT JOIN users u ON s.user_id = u.user_id " +
            "WHERE s.activity_id = #{activityId} " +
            "ORDER BY s.signup_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<ActivitySignup> findByActivityId(@Param("activityId") Integer activityId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM activity_signups WHERE activity_id = #{activityId}")
    int countByActivityId(Integer activityId);

    @Select("SELECT COUNT(*) FROM activity_signups WHERE activity_id = #{activityId} AND status IN ('pending','approved','attended')")
    int countActiveByActivityId(Integer activityId);
}
