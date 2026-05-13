package com.club.mapper;

import com.club.entity.Activity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ActivityMapper {
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.activity_id = #{activityId}")
    Activity findById(Integer activityId);
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "LIMIT #{offset}, #{limit}")
    List<Activity> findAll(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE c.founder_id = #{userId} OR c.advisor_id = #{userId} OR a.club_id = #{clubId} " +
            "LIMIT #{offset}, #{limit}")
    List<Activity> findManageable(@Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM activities a LEFT JOIN clubs c ON a.club_id = c.club_id WHERE c.founder_id = #{userId} OR c.advisor_id = #{userId} OR a.club_id = #{clubId}")
    int countManageable(@Param("userId") Integer userId, @Param("clubId") Integer clubId);
    
    @Select("SELECT COUNT(*) FROM activities")
    int countAll();

    @Select("SELECT COUNT(DISTINCT club_id) FROM activities WHERE status IN ('approved', 'completed')")
    int countClubsWithApprovedActivities();
    
    @Insert("INSERT INTO activities(club_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status) VALUES(#{clubId}, #{title}, #{content}, #{type}, #{maxParticipants}, #{registrationDeadline}, #{organizer}, #{contact}, #{startTime}, #{endTime}, #{location}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "activityId")
    int insert(Activity activity);
    
    @Update("UPDATE activities SET title = #{title}, content = #{content}, type = #{type}, max_participants = #{maxParticipants}, registration_deadline = #{registrationDeadline}, organizer = #{organizer}, contact = #{contact}, start_time = #{startTime}, end_time = #{endTime}, location = #{location}, status = #{status} WHERE activity_id = #{activityId}")
    int update(Activity activity);
    
    @Delete("DELETE FROM activities WHERE activity_id = #{activityId}")
    int deleteById(Integer activityId);
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.club_id = #{clubId}")
    List<Activity> findByClubIdSimple(Integer clubId);
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.club_id = #{clubId} LIMIT #{offset}, #{limit}")
    List<Activity> findByClubId(@Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM activities WHERE club_id = #{clubId}")
    int countByClubId(Integer clubId);

    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.club_id = #{clubId} AND a.status = #{status} LIMIT #{offset}, #{limit}")
    List<Activity> findByClubIdAndStatus(@Param("clubId") Integer clubId, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM activities WHERE club_id = #{clubId} AND status = #{status}")
    int countByClubIdAndStatus(@Param("clubId") Integer clubId, @Param("status") String status);
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.status = #{status} LIMIT #{offset}, #{limit}")
    List<Activity> findByStatus(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM activities WHERE status = #{status}")
    int countByStatus(String status);
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.title LIKE CONCAT('%',#{keyword},'%') LIMIT #{offset}, #{limit}")
    List<Activity> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM activities WHERE title LIKE CONCAT('%',#{keyword},'%')")
    int searchCount(String keyword);

    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.status = #{status} AND a.title LIKE CONCAT('%',#{keyword},'%') LIMIT #{offset}, #{limit}")
    List<Activity> searchByStatus(@Param("keyword") String keyword, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM activities WHERE status = #{status} AND title LIKE CONCAT('%',#{keyword},'%')")
    int searchCountByStatus(@Param("keyword") String keyword, @Param("status") String status);

    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE (c.founder_id = #{userId} OR c.advisor_id = #{userId} OR a.club_id = #{clubId}) " +
            "AND a.title LIKE CONCAT('%',#{keyword},'%') LIMIT #{offset}, #{limit}")
    List<Activity> searchManageable(@Param("keyword") String keyword, @Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM activities a LEFT JOIN clubs c ON a.club_id = c.club_id WHERE (c.founder_id = #{userId} OR c.advisor_id = #{userId} OR a.club_id = #{clubId}) AND a.title LIKE CONCAT('%',#{keyword},'%')")
    int searchManageableCount(@Param("keyword") String keyword, @Param("userId") Integer userId, @Param("clubId") Integer clubId);

    @Select("SELECT c.club_id, c.club_name, COUNT(a.activity_id) AS activity_count, COUNT(DISTINCT cm.user_id) AS member_count " +
            "FROM clubs c " +
            "LEFT JOIN activities a ON c.club_id = a.club_id " +
            "LEFT JOIN club_members cm ON c.club_id = cm.club_id " +
            "GROUP BY c.club_id, c.club_name " +
            "ORDER BY activity_count DESC, member_count DESC")
    List<java.util.Map<String, Object>> clubRankingStats();
}
