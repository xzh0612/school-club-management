package com.club.mapper;

import com.club.entity.Activity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ActivityMapper {
    
    @Select("SELECT a.*, c.club_name " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.activity_id = #{activityId}")
    Activity findById(Integer activityId);
    
    @Select("SELECT a.*, c.club_name " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "LIMIT #{offset}, #{limit}")
    List<Activity> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM activities")
    int countAll();
    
    @Insert("INSERT INTO activities(club_id, title, content, start_time, end_time, location, status) VALUES(#{clubId}, #{title}, #{content}, #{startTime}, #{endTime}, #{location}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "activityId")
    int insert(Activity activity);
    
    @Update("UPDATE activities SET title = #{title}, content = #{content}, start_time = #{startTime}, end_time = #{endTime}, location = #{location}, status = #{status} WHERE activity_id = #{activityId}")
    int update(Activity activity);
    
    @Delete("DELETE FROM activities WHERE activity_id = #{activityId}")
    int deleteById(Integer activityId);
    
    @Select("SELECT a.*, c.club_name " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.club_id = #{clubId}")
    List<Activity> findByClubIdSimple(Integer clubId);
    
    @Select("SELECT a.*, c.club_name " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.club_id = #{clubId} LIMIT #{offset}, #{limit}")
    List<Activity> findByClubId(@Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM activities WHERE club_id = #{clubId}")
    int countByClubId(Integer clubId);
    
    @Select("SELECT a.*, c.club_name " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.status = #{status} LIMIT #{offset}, #{limit}")
    List<Activity> findByStatus(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM activities WHERE status = #{status}")
    int countByStatus(String status);
    
    @Select("SELECT a.*, c.club_name " +
            "FROM activities a " +
            "LEFT JOIN clubs c ON a.club_id = c.club_id " +
            "WHERE a.title LIKE CONCAT('%',#{keyword},'%') LIMIT #{offset}, #{limit}")
    List<Activity> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM activities WHERE title LIKE CONCAT('%',#{keyword},'%')")
    int searchCount(String keyword);
}