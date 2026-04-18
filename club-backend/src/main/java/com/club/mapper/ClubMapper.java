package com.club.mapper;

import com.club.entity.Club;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ClubMapper {
    
    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE c.club_id = #{clubId}")
    Club findById(Integer clubId);
    
    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "LIMIT #{offset}, #{limit}")
    List<Club> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM clubs")
    int countAll();
    
    @Insert("INSERT INTO clubs(club_name, description, founder_id, advisor_id, status) VALUES(#{clubName}, #{description}, #{founderId}, #{advisorId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "clubId")
    int insert(Club club);
    
    @Update("UPDATE clubs SET club_name = #{clubName}, description = #{description}, advisor_id = #{advisorId}, status = #{status} WHERE club_id = #{clubId}")
    int update(Club club);
    
    @Delete("DELETE FROM clubs WHERE club_id = #{clubId}")
    int deleteById(Integer clubId);
    
    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE c.status = #{status} LIMIT #{offset}, #{limit}")
    List<Club> findByStatus(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM clubs WHERE status = #{status}")
    int countByStatus(String status);
    
    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE c.club_name LIKE CONCAT('%',#{keyword},'%') LIMIT #{offset}, #{limit}")
    List<Club> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM clubs WHERE club_name LIKE CONCAT('%',#{keyword},'%')")
    int searchCount(String keyword);
}