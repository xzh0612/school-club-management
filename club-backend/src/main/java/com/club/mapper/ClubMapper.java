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

    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE c.founder_id = #{userId} OR c.advisor_id = #{userId} OR c.club_id = #{clubId} " +
            "LIMIT #{offset}, #{limit}")
    List<Club> findManageable(@Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM clubs WHERE founder_id = #{userId} OR advisor_id = #{userId} OR club_id = #{clubId}")
    int countManageable(@Param("userId") Integer userId, @Param("clubId") Integer clubId);
    
    @Select("SELECT COUNT(*) FROM clubs")
    int countAll();
    
    @Insert("INSERT INTO clubs(club_name, description, club_type, founder_id, advisor_id, status) VALUES(#{clubName}, #{description}, #{clubType}, #{founderId}, #{advisorId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "clubId")
    int insert(Club club);
    
    @Update("UPDATE clubs SET club_name = #{clubName}, description = #{description}, club_type = COALESCE(#{clubType}, club_type), advisor_id = #{advisorId}, status = #{status} WHERE club_id = #{clubId}")
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

    @Select("SELECT YEAR(create_time) AS year, COUNT(*) AS count FROM clubs GROUP BY YEAR(create_time) ORDER BY YEAR(create_time)")
    List<java.util.Map<String, Object>> countCreatedByYear();
    
    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE c.club_name LIKE CONCAT('%',#{keyword},'%') LIMIT #{offset}, #{limit}")
    List<Club> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM clubs WHERE club_name LIKE CONCAT('%',#{keyword},'%')")
    int searchCount(String keyword);

    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE c.status = #{status} AND c.club_name LIKE CONCAT('%',#{keyword},'%') LIMIT #{offset}, #{limit}")
    List<Club> searchByStatus(@Param("keyword") String keyword, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM clubs WHERE status = #{status} AND club_name LIKE CONCAT('%',#{keyword},'%')")
    int searchCountByStatus(@Param("keyword") String keyword, @Param("status") String status);

    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE (c.founder_id = #{userId} OR c.advisor_id = #{userId} OR c.club_id = #{clubId}) " +
            "AND c.club_name LIKE CONCAT('%',#{keyword},'%') LIMIT #{offset}, #{limit}")
    List<Club> searchManageable(@Param("keyword") String keyword, @Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM clubs c WHERE (c.founder_id = #{userId} OR c.advisor_id = #{userId} OR c.club_id = #{clubId}) AND c.club_name LIKE CONCAT('%',#{keyword},'%')")
    int searchManageableCount(@Param("keyword") String keyword, @Param("userId") Integer userId, @Param("clubId") Integer clubId);
}
