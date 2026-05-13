package com.club.mapper;

import com.club.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT u.*, c.club_name FROM users u LEFT JOIN clubs c ON u.club_id = c.club_id WHERE u.user_id = #{userId}")
    User findById(Integer userId);
    
    @Select("SELECT u.*, c.club_name FROM users u LEFT JOIN clubs c ON u.club_id = c.club_id WHERE u.username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT u.*, c.club_name FROM users u LEFT JOIN clubs c ON u.club_id = c.club_id WHERE u.role = #{role} ORDER BY u.create_time DESC, u.user_id DESC LIMIT #{offset}, #{limit}")
    List<User> findByRole(@Param("role") String role, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT u.*, c.club_name FROM users u LEFT JOIN clubs c ON u.club_id = c.club_id " +
            "WHERE 1 = 1 " +
            "<if test='role != null and role != \"\"'>AND u.role = #{role} </if>" +
            "<if test='status != null and status != \"\"'>AND u.status = #{status} </if>" +
            "ORDER BY u.create_time DESC, u.user_id DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<User> findFiltered(@Param("role") String role, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT u.*, c.club_name FROM users u INNER JOIN club_members cm ON u.user_id = cm.user_id LEFT JOIN clubs c ON cm.club_id = c.club_id WHERE cm.club_id = #{clubId} ORDER BY cm.join_time DESC, u.user_id DESC LIMIT #{offset}, #{limit}")
    List<User> findByClubId(@Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT c.club_id AS id, c.club_name AS clubName, cm.role, cm.join_time AS joinTime " +
            "FROM club_members cm INNER JOIN clubs c ON cm.club_id = c.club_id " +
            "WHERE cm.user_id = #{userId} ORDER BY cm.join_time DESC")
    List<java.util.Map<String, Object>> findUserClubs(Integer userId);

    @Select("SELECT s.signup_id AS id, a.title AS activityName, c.club_name AS clubName, a.start_time AS activityTime, s.status AS checkStatus, '' AS rating " +
            "FROM activity_signups s INNER JOIN activities a ON s.activity_id = a.activity_id " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE s.user_id = #{userId} ORDER BY a.start_time DESC")
    List<java.util.Map<String, Object>> findUserActivities(Integer userId);

    @Select("SELECT COUNT(*) FROM club_members WHERE club_id = #{clubId}")
    int countByClubId(Integer clubId);
    
    @Select("SELECT COUNT(*) FROM users WHERE role = #{role}")
    int countByRole(String role);

    @Select("<script>" +
            "SELECT COUNT(*) FROM users u WHERE 1 = 1 " +
            "<if test='role != null and role != \"\"'>AND u.role = #{role} </if>" +
            "<if test='status != null and status != \"\"'>AND u.status = #{status} </if>" +
            "</script>")
    int countFiltered(@Param("role") String role, @Param("status") String status);
    
    @Select("SELECT u.*, c.club_name FROM users u LEFT JOIN clubs c ON u.club_id = c.club_id ORDER BY u.create_time DESC, u.user_id DESC LIMIT #{offset}, #{limit}")
    List<User> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM users")
    int countAll();

    @Select("SELECT COUNT(DISTINCT user_id) FROM club_members WHERE status = 'active'")
    int countActiveClubMembers();

    @Select("SELECT COUNT(DISTINCT cm.user_id) " +
            "FROM club_members cm " +
            "INNER JOIN users u ON cm.user_id = u.user_id " +
            "WHERE cm.status = 'active' AND u.role = 'student'")
    int countActiveStudentsInClubs();

    @Select("SELECT COUNT(DISTINCT cm.user_id) FROM club_members cm " +
            "INNER JOIN clubs c ON cm.club_id = c.club_id " +
            "INNER JOIN users u ON cm.user_id = u.user_id " +
            "WHERE c.advisor_id = #{advisorId} AND cm.status = 'active' AND u.role = 'student'")
    int countActiveStudentsInAdvisorClubs(Integer advisorId);

    @Select("SELECT COUNT(DISTINCT cm.user_id) FROM club_members cm " +
            "INNER JOIN clubs c ON cm.club_id = c.club_id " +
            "INNER JOIN users u ON cm.user_id = u.user_id " +
            "WHERE c.advisor_id = #{advisorId} AND u.role = 'student'")
    int countStudentsInAdvisorClubs(Integer advisorId);
    
    @Insert("INSERT INTO users(username, password, real_name, role, status, club_id, student_id, department, class_name, register_time, email, phone, token_version) VALUES(#{username}, #{password}, #{realName}, #{role}, COALESCE(#{status}, 'active'), #{clubId}, #{studentId}, #{department}, #{className}, COALESCE(#{registerTime}, NOW()), #{email}, #{phone}, COALESCE(#{tokenVersion}, 0))")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);
    
    @Update("UPDATE users SET username = #{username}, password = #{password}, real_name = #{realName}, role = #{role}, status = #{status}, club_id = #{clubId}, student_id = #{studentId}, department = #{department}, class_name = #{className}, email = #{email}, phone = #{phone} WHERE user_id = #{userId}")
    int update(User user);

    @Update("UPDATE users SET last_login_time = NOW() WHERE user_id = #{userId}")
    int updateLastLoginTime(Integer userId);

    @Update("UPDATE users SET token_version = token_version + 1 WHERE user_id = #{userId}")
    int incrementTokenVersion(Integer userId);
    
    @Update("UPDATE users SET status = 'inactive', token_version = token_version + 1 WHERE user_id = #{userId}")
    int deleteById(Integer userId);
    
    @Select("SELECT u.*, c.club_name FROM users u LEFT JOIN clubs c ON u.club_id = c.club_id WHERE u.role != 'admin' AND (u.username LIKE CONCAT('%',#{keyword},'%') OR u.real_name LIKE CONCAT('%',#{keyword},'%') OR u.student_id LIKE CONCAT('%',#{keyword},'%')) ORDER BY u.create_time DESC, u.user_id DESC LIMIT #{offset}, #{limit}")
    List<User> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT u.*, c.club_name FROM users u LEFT JOIN clubs c ON u.club_id = c.club_id " +
            "WHERE u.role != 'admin' " +
            "AND (u.username LIKE CONCAT('%',#{keyword},'%') OR u.real_name LIKE CONCAT('%',#{keyword},'%') OR u.student_id LIKE CONCAT('%',#{keyword},'%')) " +
            "<if test='role != null and role != \"\"'>AND u.role = #{role} </if>" +
            "<if test='status != null and status != \"\"'>AND u.status = #{status} </if>" +
            "ORDER BY u.create_time DESC, u.user_id DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<User> searchFiltered(@Param("keyword") String keyword, @Param("role") String role, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM users WHERE role != 'admin' AND (username LIKE CONCAT('%',#{keyword},'%') OR real_name LIKE CONCAT('%',#{keyword},'%') OR student_id LIKE CONCAT('%',#{keyword},'%'))")
    int searchCount(String keyword);

    @Select("<script>" +
            "SELECT COUNT(*) FROM users u WHERE u.role != 'admin' " +
            "AND (u.username LIKE CONCAT('%',#{keyword},'%') OR u.real_name LIKE CONCAT('%',#{keyword},'%') OR u.student_id LIKE CONCAT('%',#{keyword},'%')) " +
            "<if test='role != null and role != \"\"'>AND u.role = #{role} </if>" +
            "<if test='status != null and status != \"\"'>AND u.status = #{status} </if>" +
            "</script>")
    int searchCountFiltered(@Param("keyword") String keyword, @Param("role") String role, @Param("status") String status);
}
