package com.club.mapper;

import com.club.entity.Activity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ActivityMapper {
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id AND s.status IN ('pending','approved','attended')) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.activity_id = #{activityId}")
    Activity findById(Integer activityId);

    @Select("SELECT activity_id, club_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status, create_time, update_time " +
            "FROM activities WHERE activity_id = #{activityId} FOR UPDATE")
    Activity findByIdForUpdate(Integer activityId);
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id AND s.status IN ('pending','approved','attended')) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "ORDER BY a.start_time DESC, a.activity_id DESC LIMIT #{offset}, #{limit}")
    List<Activity> findAll(@Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id AND s.status IN ('pending','approved','attended')) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE (" +
            "<if test='includeAdvisor'>c.advisor_id = #{userId} OR </if>" +
            "a.club_id = #{clubId} " +
            "OR EXISTS (SELECT 1 FROM club_members cm WHERE cm.club_id = a.club_id AND cm.user_id = #{userId} AND cm.role IN ('leader','club_leader') AND cm.status = 'active')) " +
            "ORDER BY a.start_time DESC, a.activity_id DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Activity> findManageable(@Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("includeAdvisor") boolean includeAdvisor, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM activities a LEFT JOIN clubs c ON a.club_id = c.club_id WHERE (" +
            "<if test='includeAdvisor'>c.advisor_id = #{userId} OR </if>" +
            "a.club_id = #{clubId} " +
            "OR EXISTS (SELECT 1 FROM club_members cm WHERE cm.club_id = a.club_id AND cm.user_id = #{userId} AND cm.role IN ('leader','club_leader') AND cm.status = 'active'))" +
            "</script>")
    int countManageable(@Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("includeAdvisor") boolean includeAdvisor);
    
    @Select("SELECT COUNT(*) FROM activities")
    int countAll();

    @Select("SELECT COUNT(*) FROM activities a INNER JOIN clubs c ON a.club_id = c.club_id WHERE c.advisor_id = #{advisorId}")
    int countByAdvisor(Integer advisorId);

    @Select("SELECT COUNT(DISTINCT club_id) FROM activities WHERE status IN ('approved', 'completed')")
    int countClubsWithApprovedActivities();

    @Select("SELECT COUNT(DISTINCT a.club_id) FROM activities a INNER JOIN clubs c ON a.club_id = c.club_id WHERE c.advisor_id = #{advisorId} AND a.status IN ('approved', 'completed')")
    int countAdvisorClubsWithApprovedActivities(Integer advisorId);
    
    @Insert("INSERT INTO activities(club_id, title, content, type, max_participants, registration_deadline, organizer, contact, start_time, end_time, location, status) VALUES(#{clubId}, #{title}, #{content}, #{type}, #{maxParticipants}, #{registrationDeadline}, #{organizer}, #{contact}, #{startTime}, #{endTime}, #{location}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "activityId")
    int insert(Activity activity);
    
    @Update("UPDATE activities SET title = #{title}, content = #{content}, type = #{type}, max_participants = #{maxParticipants}, registration_deadline = #{registrationDeadline}, organizer = #{organizer}, contact = #{contact}, start_time = #{startTime}, end_time = #{endTime}, location = #{location}, status = #{status} WHERE activity_id = #{activityId}")
    int update(Activity activity);
    
    @Update("UPDATE activities SET status = 'archived' WHERE activity_id = #{activityId}")
    int deleteById(Integer activityId);
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id AND s.status IN ('pending','approved','attended')) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.club_id = #{clubId}")
    List<Activity> findByClubIdSimple(Integer clubId);
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id AND s.status IN ('pending','approved','attended')) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.club_id = #{clubId} ORDER BY a.start_time DESC, a.activity_id DESC LIMIT #{offset}, #{limit}")
    List<Activity> findByClubId(@Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM activities WHERE club_id = #{clubId}")
    int countByClubId(Integer clubId);

    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id AND s.status IN ('pending','approved','attended')) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.club_id = #{clubId} AND a.status = #{status} ORDER BY a.start_time DESC, a.activity_id DESC LIMIT #{offset}, #{limit}")
    List<Activity> findByClubIdAndStatus(@Param("clubId") Integer clubId, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM activities WHERE club_id = #{clubId} AND status = #{status}")
    int countByClubIdAndStatus(@Param("clubId") Integer clubId, @Param("status") String status);
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id AND s.status IN ('pending','approved','attended')) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.status = #{status} ORDER BY a.start_time DESC, a.activity_id DESC LIMIT #{offset}, #{limit}")
    List<Activity> findByStatus(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM activities WHERE status = #{status}")
    int countByStatus(String status);

    @Select("SELECT COUNT(*) FROM activities a INNER JOIN clubs c ON a.club_id = c.club_id WHERE c.advisor_id = #{advisorId} AND a.status = #{status}")
    int countByAdvisorAndStatus(@Param("advisorId") Integer advisorId, @Param("status") String status);
    
    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id AND s.status IN ('pending','approved','attended')) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.title LIKE CONCAT('%',#{keyword},'%') ORDER BY a.start_time DESC, a.activity_id DESC LIMIT #{offset}, #{limit}")
    List<Activity> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM activities WHERE title LIKE CONCAT('%',#{keyword},'%')")
    int searchCount(String keyword);

    @Select("SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id AND s.status IN ('pending','approved','attended')) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.status = #{status} AND a.title LIKE CONCAT('%',#{keyword},'%') ORDER BY a.start_time DESC, a.activity_id DESC LIMIT #{offset}, #{limit}")
    List<Activity> searchByStatus(@Param("keyword") String keyword, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM activities WHERE status = #{status} AND title LIKE CONCAT('%',#{keyword},'%')")
    int searchCountByStatus(@Param("keyword") String keyword, @Param("status") String status);

    @Select("<script>" +
            "SELECT a.*, c.club_name, (SELECT COUNT(*) FROM activity_signups s WHERE s.activity_id = a.activity_id AND s.status IN ('pending','approved','attended')) AS current_participants " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE (" +
            "<if test='includeAdvisor'>c.advisor_id = #{userId} OR </if>" +
            "a.club_id = #{clubId} " +
            "OR EXISTS (SELECT 1 FROM club_members cm WHERE cm.club_id = a.club_id AND cm.user_id = #{userId} AND cm.role IN ('leader','club_leader') AND cm.status = 'active')) " +
            "AND a.title LIKE CONCAT('%',#{keyword},'%') ORDER BY a.start_time DESC, a.activity_id DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Activity> searchManageable(@Param("keyword") String keyword, @Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("includeAdvisor") boolean includeAdvisor, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM activities a LEFT JOIN clubs c ON a.club_id = c.club_id WHERE (" +
            "<if test='includeAdvisor'>c.advisor_id = #{userId} OR </if>" +
            "a.club_id = #{clubId} " +
            "OR EXISTS (SELECT 1 FROM club_members cm WHERE cm.club_id = a.club_id AND cm.user_id = #{userId} AND cm.role IN ('leader','club_leader') AND cm.status = 'active')) " +
            "AND a.title LIKE CONCAT('%',#{keyword},'%')" +
            "</script>")
    int searchManageableCount(@Param("keyword") String keyword, @Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("includeAdvisor") boolean includeAdvisor);

    @Select("SELECT c.club_id, c.club_name, COUNT(a.activity_id) AS activity_count, COUNT(DISTINCT cm.user_id) AS member_count " +
            "FROM clubs c " +
            "LEFT JOIN activities a ON c.club_id = a.club_id AND a.status IN ('approved', 'completed') " +
            "LEFT JOIN club_members cm ON c.club_id = cm.club_id AND cm.status = 'active' " +
            "GROUP BY c.club_id, c.club_name " +
            "ORDER BY activity_count DESC, member_count DESC")
    List<java.util.Map<String, Object>> clubRankingStats();

    @Select("SELECT c.club_id, c.club_name, COUNT(a.activity_id) AS activity_count, COUNT(DISTINCT cm.user_id) AS member_count " +
            "FROM clubs c " +
            "LEFT JOIN activities a ON c.club_id = a.club_id AND a.status IN ('approved', 'completed') " +
            "LEFT JOIN club_members cm ON c.club_id = cm.club_id AND cm.status = 'active' " +
            "WHERE c.advisor_id = #{advisorId} " +
            "GROUP BY c.club_id, c.club_name " +
            "ORDER BY activity_count DESC, member_count DESC")
    List<java.util.Map<String, Object>> clubRankingStatsForAdvisor(Integer advisorId);
}
