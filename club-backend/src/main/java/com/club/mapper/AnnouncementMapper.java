package com.club.mapper;

import com.club.entity.Announcement;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AnnouncementMapper {
    
    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE a.announcement_id = #{announcementId}")
    Announcement findById(Integer announcementId);

    @Select("<script>" +
            "SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE 1 = 1 " +
            "<if test='status != null and status != \"\"'>AND a.status = #{status} </if>" +
            "<if test='targetType != null and targetType != \"\"'>AND a.target_type = #{targetType} </if>" +
            "<if test='isTop != null'>AND a.is_top = #{isTop} </if>" +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Announcement> findByFilters(@Param("status") String status, @Param("targetType") String targetType, @Param("isTop") Boolean isTop, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM announcements a WHERE 1 = 1 " +
            "<if test='status != null and status != \"\"'>AND a.status = #{status} </if>" +
            "<if test='targetType != null and targetType != \"\"'>AND a.target_type = #{targetType} </if>" +
            "<if test='isTop != null'>AND a.is_top = #{isTop} </if>" +
            "</script>")
    int countByFilters(@Param("status") String status, @Param("targetType") String targetType, @Param("isTop") Boolean isTop);

    @Select("<script>" +
            "SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE a.status = 'published' " +
            "<if test='targetType != null and targetType != \"\"'>AND a.target_type = #{targetType} </if>" +
            "<if test='isTop != null'>AND a.is_top = #{isTop} </if>" +
            "<choose>" +
            "<when test='clubId != null'>AND (a.target_type = 'all' OR (a.target_type = 'club' AND a.target_id = #{clubId})) </when>" +
            "<otherwise>AND a.target_type = 'all' </otherwise>" +
            "</choose>" +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Announcement> findVisible(@Param("clubId") Integer clubId, @Param("targetType") String targetType, @Param("isTop") Boolean isTop, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM announcements a " +
            "WHERE a.status = 'published' " +
            "<if test='targetType != null and targetType != \"\"'>AND a.target_type = #{targetType} </if>" +
            "<if test='isTop != null'>AND a.is_top = #{isTop} </if>" +
            "<choose>" +
            "<when test='clubId != null'>AND (a.target_type = 'all' OR (a.target_type = 'club' AND a.target_id = #{clubId})) </when>" +
            "<otherwise>AND a.target_type = 'all' </otherwise>" +
            "</choose>" +
            "</script>")
    int countVisible(@Param("clubId") Integer clubId, @Param("targetType") String targetType, @Param("isTop") Boolean isTop);

    @Select("<script>" +
            "SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE a.target_type = 'club' AND a.target_id = #{clubId} " +
            "<if test='status != null and status != \"\"'>AND a.status = #{status} </if>" +
            "<if test='isTop != null'>AND a.is_top = #{isTop} </if>" +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Announcement> findManageable(@Param("clubId") Integer clubId, @Param("status") String status, @Param("isTop") Boolean isTop, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM announcements a " +
            "WHERE a.target_type = 'club' AND a.target_id = #{clubId} " +
            "<if test='status != null and status != \"\"'>AND a.status = #{status} </if>" +
            "<if test='isTop != null'>AND a.is_top = #{isTop} </if>" +
            "</script>")
    int countManageable(@Param("clubId") Integer clubId, @Param("status") String status, @Param("isTop") Boolean isTop);

    @Insert("INSERT INTO announcements(title, content, publisher_id, target_type, target_id, status, is_top) VALUES(#{title}, #{content}, #{publisherId}, #{targetType}, #{targetId}, #{status}, #{isTop})")
    @Options(useGeneratedKeys = true, keyProperty = "announcementId")
    int insert(Announcement announcement);
    
    @Update("UPDATE announcements SET title = #{title}, content = #{content}, target_type = #{targetType}, target_id = #{targetId}, is_top = #{isTop}, status = #{status}, update_time = NOW() WHERE announcement_id = #{announcementId}")
    int update(Announcement announcement);
    
    @Delete("DELETE FROM announcements WHERE announcement_id = #{announcementId}")
    int deleteById(Integer announcementId);
    
    @Update("UPDATE announcements SET view_count = view_count + 1 WHERE announcement_id = #{announcementId}")
    int incrementView(Integer announcementId);
    
    @Update("UPDATE announcements SET status = #{status}, publish_time = CASE WHEN #{status} = 'published' THEN NOW() ELSE publish_time END, update_time = NOW() WHERE announcement_id = #{announcementId}")
    int updateStatus(Announcement announcement);
    
    @Update("UPDATE announcements SET is_top = #{isTop}, update_time = NOW() WHERE announcement_id = #{announcementId}")
    int updateTopStatus(Announcement announcement);
    
    @Select("<script>" +
            "SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE a.title LIKE CONCAT('%',#{keyword},'%') " +
            "<if test='status != null and status != \"\"'>AND a.status = #{status} </if>" +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Announcement> search(@Param("keyword") String keyword, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM announcements WHERE title LIKE CONCAT('%',#{keyword},'%') " +
            "<if test='status != null and status != \"\"'>AND status = #{status} </if>" +
            "</script>")
    int searchCount(@Param("keyword") String keyword, @Param("status") String status);

    @Select("<script>" +
            "SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE a.title LIKE CONCAT('%',#{keyword},'%') " +
            "AND a.status = 'published' " +
            "<choose>" +
            "<when test='clubId != null'>AND (a.target_type = 'all' OR (a.target_type = 'club' AND a.target_id = #{clubId})) </when>" +
            "<otherwise>AND a.target_type = 'all' </otherwise>" +
            "</choose>" +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Announcement> searchVisible(@Param("keyword") String keyword, @Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM announcements a " +
            "WHERE a.title LIKE CONCAT('%',#{keyword},'%') " +
            "AND a.status = 'published' " +
            "<choose>" +
            "<when test='clubId != null'>AND (a.target_type = 'all' OR (a.target_type = 'club' AND a.target_id = #{clubId})) </when>" +
            "<otherwise>AND a.target_type = 'all' </otherwise>" +
            "</choose>" +
            "</script>")
    int searchVisibleCount(@Param("keyword") String keyword, @Param("clubId") Integer clubId);

    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE a.target_type = 'club' AND a.target_id = #{clubId} AND a.title LIKE CONCAT('%',#{keyword},'%') " +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Announcement> searchManageable(@Param("keyword") String keyword, @Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM announcements WHERE target_type = 'club' AND target_id = #{clubId} AND title LIKE CONCAT('%',#{keyword},'%')")
    int searchManageableCount(@Param("keyword") String keyword, @Param("clubId") Integer clubId);
}
