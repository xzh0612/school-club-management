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
    
    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Announcement> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM announcements")
    int countAll();
    
    @Insert("INSERT INTO announcements(title, content, publisher_id, target_type, target_id, is_top) VALUES(#{title}, #{content}, #{publisherId}, #{targetType}, #{targetId}, #{isTop})")
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
    
    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE a.target_type = #{targetType} " +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Announcement> findByTargetType(@Param("targetType") String targetType, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM announcements WHERE target_type = #{targetType}")
    int countByTargetType(String targetType);
    
    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE a.is_top = #{isTop} " +
            "ORDER BY a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Announcement> findByIsTop(@Param("isTop") Boolean isTop, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM announcements WHERE is_top = #{isTop}")
    int countByIsTop(Boolean isTop);
    
    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE a.target_type = 'all' OR (a.target_type = 'club' AND a.target_id = #{clubId}) " +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Announcement> findByTarget(@Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.publisher_id = u.user_id " +
            "WHERE a.title LIKE CONCAT('%',#{keyword},'%') " +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Announcement> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM announcements WHERE title LIKE CONCAT('%',#{keyword},'%')")
    int searchCount(String keyword);
}