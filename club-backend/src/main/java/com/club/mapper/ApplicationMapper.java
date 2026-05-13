package com.club.mapper;

import com.club.entity.Application;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ApplicationMapper {

    @Select("SELECT app.*, u.real_name AS user_name, u.student_id AS user_sid, c.club_name, r.title AS recruitment_title " +
            "FROM applications app " +
            "LEFT JOIN users u ON app.user_id = u.user_id " +
            "LEFT JOIN clubs c ON app.club_id = c.club_id " +
            "LEFT JOIN recruitments r ON app.recruitment_id = r.recruitment_id " +
            "WHERE app.application_id = #{applicationId}")
    Application findById(Integer applicationId);

    @Select("SELECT app.*, u.real_name AS user_name, u.student_id AS user_sid, c.club_name, r.title AS recruitment_title " +
            "FROM applications app " +
            "LEFT JOIN users u ON app.user_id = u.user_id " +
            "LEFT JOIN clubs c ON app.club_id = c.club_id " +
            "LEFT JOIN recruitments r ON app.recruitment_id = r.recruitment_id " +
            "ORDER BY app.apply_time DESC LIMIT #{offset}, #{limit}")
    List<Application> findAll(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM applications")
    int countAll();

    @Select("SELECT app.*, u.real_name AS user_name, u.student_id AS user_sid, c.club_name, r.title AS recruitment_title " +
            "FROM applications app " +
            "LEFT JOIN users u ON app.user_id = u.user_id " +
            "LEFT JOIN clubs c ON app.club_id = c.club_id " +
            "LEFT JOIN recruitments r ON app.recruitment_id = r.recruitment_id " +
            "WHERE app.club_id = #{clubId} ORDER BY app.apply_time DESC LIMIT #{offset}, #{limit}")
    List<Application> findByClubId(@Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM applications WHERE club_id = #{clubId}")
    int countByClubId(Integer clubId);

    @Select("SELECT app.*, u.real_name AS user_name, u.student_id AS user_sid, c.club_name, r.title AS recruitment_title " +
            "FROM applications app " +
            "LEFT JOIN users u ON app.user_id = u.user_id " +
            "LEFT JOIN clubs c ON app.club_id = c.club_id " +
            "LEFT JOIN recruitments r ON app.recruitment_id = r.recruitment_id " +
            "WHERE app.club_id = #{clubId} AND app.status = #{status} ORDER BY app.apply_time DESC LIMIT #{offset}, #{limit}")
    List<Application> findByClubIdAndStatus(@Param("clubId") Integer clubId, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM applications WHERE club_id = #{clubId} AND status = #{status}")
    int countByClubIdAndStatus(@Param("clubId") Integer clubId, @Param("status") String status);

    @Select("SELECT app.*, u.real_name AS user_name, u.student_id AS user_sid, c.club_name, r.title AS recruitment_title " +
            "FROM applications app " +
            "LEFT JOIN users u ON app.user_id = u.user_id " +
            "LEFT JOIN clubs c ON app.club_id = c.club_id " +
            "LEFT JOIN recruitments r ON app.recruitment_id = r.recruitment_id " +
            "WHERE app.user_id = #{userId} ORDER BY app.apply_time DESC LIMIT #{offset}, #{limit}")
    List<Application> findByUserId(@Param("userId") Integer userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM applications WHERE user_id = #{userId}")
    int countByUserId(Integer userId);

    @Select("SELECT COUNT(*) FROM applications WHERE user_id = #{userId} AND club_id = #{clubId} AND status = 'pending'")
    int countPendingByUserAndClub(@Param("userId") Integer userId, @Param("clubId") Integer clubId);

    @Select("SELECT COUNT(*) FROM applications WHERE club_id = #{clubId} AND status = 'approved'")
    int countApprovedByClubId(Integer clubId);

    @Select("SELECT COUNT(*) FROM applications WHERE recruitment_id = #{recruitmentId} AND status = 'approved'")
    int countApprovedByRecruitmentId(Integer recruitmentId);

    @Insert("INSERT INTO applications(user_id, club_id, recruitment_id, introduction, status) VALUES(#{userId}, #{clubId}, #{recruitmentId}, #{introduction}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "applicationId")
    int insert(Application application);

    @Update("UPDATE applications SET status = #{status}, comments = #{comments}, review_time = NOW() WHERE application_id = #{applicationId}")
    int updateReview(Application application);
}
