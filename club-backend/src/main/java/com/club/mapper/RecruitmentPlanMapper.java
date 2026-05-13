package com.club.mapper;

import com.club.entity.RecruitmentPlan;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecruitmentPlanMapper {

    @Select("SELECT rp.recruitment_id, rp.club_id, rp.title, rp.quota, rp.requirements, rp.description, rp.status, " +
            "DATE(rp.start_time) AS start_date, DATE(rp.end_time) AS end_date, rp.create_time, rp.update_time, c.club_name, " +
            "(SELECT COUNT(*) FROM applications app WHERE app.recruitment_id = rp.recruitment_id OR (app.recruitment_id IS NULL AND app.club_id = rp.club_id)) AS applied_count " +
            "FROM recruitments rp LEFT JOIN clubs c ON rp.club_id = c.club_id " +
            "ORDER BY rp.create_time DESC LIMIT #{offset}, #{limit}")
    List<RecruitmentPlan> findAll(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM recruitments")
    int countAll();

    @Select("SELECT rp.recruitment_id, rp.club_id, rp.title, rp.quota, rp.requirements, rp.description, rp.status, " +
            "DATE(rp.start_time) AS start_date, DATE(rp.end_time) AS end_date, rp.create_time, rp.update_time, c.club_name, " +
            "(SELECT COUNT(*) FROM applications app WHERE app.recruitment_id = rp.recruitment_id OR (app.recruitment_id IS NULL AND app.club_id = rp.club_id)) AS applied_count " +
            "FROM recruitments rp LEFT JOIN clubs c ON rp.club_id = c.club_id " +
            "WHERE rp.club_id = #{clubId} ORDER BY rp.create_time DESC LIMIT #{offset}, #{limit}")
    List<RecruitmentPlan> findByClubId(@Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM recruitments WHERE club_id = #{clubId}")
    int countByClubId(Integer clubId);

    @Select("SELECT recruitment_id, club_id, title, quota, requirements, description, status, " +
            "DATE(start_time) AS start_date, DATE(end_time) AS end_date, create_time, update_time " +
            "FROM recruitments WHERE recruitment_id = #{recruitmentId}")
    RecruitmentPlan findById(Integer recruitmentId);

    @Select("SELECT recruitment_id, club_id, title, quota, requirements, description, status, " +
            "DATE(start_time) AS start_date, DATE(end_time) AS end_date, create_time, update_time " +
            "FROM recruitments WHERE recruitment_id = #{recruitmentId} FOR UPDATE")
    RecruitmentPlan findByIdForUpdate(Integer recruitmentId);

    @Insert("INSERT INTO recruitments(club_id, title, quota, requirements, description, status, start_time, end_time) VALUES(#{clubId}, #{title}, #{quota}, #{requirements}, #{description}, #{status}, #{startDate}, #{endDate})")
    @Options(useGeneratedKeys = true, keyProperty = "recruitmentId")
    int insert(RecruitmentPlan plan);

    @Update("UPDATE recruitments SET club_id = #{clubId}, title = #{title}, quota = #{quota}, requirements = #{requirements}, description = #{description}, status = #{status}, start_time = #{startDate}, end_time = #{endDate} WHERE recruitment_id = #{recruitmentId}")
    int update(RecruitmentPlan plan);

    @Update("UPDATE recruitments SET status = 'archived' WHERE recruitment_id = #{recruitmentId}")
    int deleteById(Integer recruitmentId);
}
