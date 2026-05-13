package com.club.mapper;

import com.club.entity.ClubMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClubMemberMapper {

    @Select("SELECT cm.member_id AS id, cm.club_id, cm.user_id, cm.role, cm.status, cm.join_time, c.club_name, u.real_name AS user_name, u.student_id " +
            "FROM club_members cm " +
            "LEFT JOIN clubs c ON cm.club_id = c.club_id " +
            "LEFT JOIN users u ON cm.user_id = u.user_id " +
            "WHERE cm.club_id = #{clubId} " +
            "ORDER BY cm.join_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<ClubMember> findByClubId(@Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM club_members WHERE club_id = #{clubId}")
    int countByClubId(Integer clubId);

    @Select("SELECT member_id AS id, club_id, user_id, role, status, join_time FROM club_members WHERE club_id = #{clubId} AND user_id = #{userId}")
    ClubMember findByClubAndUser(@Param("clubId") Integer clubId, @Param("userId") Integer userId);

    @Insert("INSERT INTO club_members(club_id, user_id, role, status) VALUES(#{clubId}, #{userId}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ClubMember member);

    @Update("UPDATE club_members SET role = #{role}, status = #{status} WHERE member_id = #{id}")
    int update(ClubMember member);

    @Delete("DELETE FROM club_members WHERE club_id = #{clubId} AND user_id = #{userId}")
    int deleteByClubAndUser(@Param("clubId") Integer clubId, @Param("userId") Integer userId);
}
