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
            "ORDER BY c.create_time DESC, c.club_id DESC LIMIT #{offset}, #{limit}")
    List<Club> findAll(@Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE (" +
            "<if test='includeAdvisor'>c.advisor_id = #{userId} OR </if>" +
            "c.club_id = #{clubId} " +
            "OR EXISTS (SELECT 1 FROM club_members cm WHERE cm.club_id = c.club_id AND cm.user_id = #{userId} AND cm.role IN ('leader','club_leader') AND cm.status = 'active')) " +
            "ORDER BY c.create_time DESC, c.club_id DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Club> findManageable(@Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("includeAdvisor") boolean includeAdvisor, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM clubs c WHERE (" +
            "<if test='includeAdvisor'>c.advisor_id = #{userId} OR </if>" +
            "c.club_id = #{clubId} " +
            "OR EXISTS (SELECT 1 FROM club_members cm WHERE cm.club_id = c.club_id AND cm.user_id = #{userId} AND cm.role IN ('leader','club_leader') AND cm.status = 'active'))" +
            "</script>")
    int countManageable(@Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("includeAdvisor") boolean includeAdvisor);
    
    @Select("SELECT COUNT(*) FROM clubs")
    int countAll();

    @Select("SELECT COUNT(*) FROM clubs WHERE advisor_id = #{advisorId}")
    int countByAdvisor(Integer advisorId);
    
    @Insert("INSERT INTO clubs(club_name, description, club_type, founder_id, advisor_id, status) VALUES(#{clubName}, #{description}, #{clubType}, #{founderId}, #{advisorId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "clubId")
    int insert(Club club);
    
    @Update("UPDATE clubs SET club_name = #{clubName}, description = #{description}, club_type = COALESCE(#{clubType}, club_type), advisor_id = #{advisorId}, status = #{status} WHERE club_id = #{clubId}")
    int update(Club club);
    
    @Update("UPDATE clubs SET status = 'archived' WHERE club_id = #{clubId}")
    int deleteById(Integer clubId);
    
    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE c.status = #{status} ORDER BY c.create_time DESC, c.club_id DESC LIMIT #{offset}, #{limit}")
    List<Club> findByStatus(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM clubs WHERE status = #{status}")
    int countByStatus(String status);

    @Select("SELECT COUNT(*) FROM clubs WHERE advisor_id = #{advisorId} AND status = #{status}")
    int countByAdvisorAndStatus(@Param("advisorId") Integer advisorId, @Param("status") String status);

    @Select("SELECT COUNT(*) FROM clubs WHERE club_type IS NOT NULL AND club_type <> '' AND club_type <> 'general'")
    int countClassified();

    @Select("<script>" +
            "SELECT COUNT(*) FROM clubs c WHERE (" +
            "<if test='includeAdvisor'>c.advisor_id = #{userId} OR </if>" +
            "c.club_id = #{clubId} " +
            "OR EXISTS (SELECT 1 FROM club_members cm WHERE cm.club_id = c.club_id AND cm.user_id = #{userId} AND cm.role IN ('leader','club_leader') AND cm.status = 'active')) AND c.status = #{status}" +
            "</script>")
    int countManageableByStatus(@Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("includeAdvisor") boolean includeAdvisor, @Param("status") String status);

    @Select("<script>" +
            "SELECT COUNT(*) FROM clubs c WHERE (" +
            "<if test='includeAdvisor'>c.advisor_id = #{userId} OR </if>" +
            "c.club_id = #{clubId} " +
            "OR EXISTS (SELECT 1 FROM club_members cm WHERE cm.club_id = c.club_id AND cm.user_id = #{userId} AND cm.role IN ('leader','club_leader') AND cm.status = 'active')) " +
            "AND c.club_type IS NOT NULL AND c.club_type &lt;&gt; '' AND c.club_type &lt;&gt; 'general'" +
            "</script>")
    int countManageableClassified(@Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("includeAdvisor") boolean includeAdvisor);

    @Select("SELECT YEAR(create_time) AS year, COUNT(*) AS count FROM clubs GROUP BY YEAR(create_time) ORDER BY YEAR(create_time)")
    List<java.util.Map<String, Object>> countCreatedByYear();

    @Select("SELECT YEAR(create_time) AS year, COUNT(*) AS count FROM clubs WHERE advisor_id = #{advisorId} GROUP BY YEAR(create_time) ORDER BY YEAR(create_time)")
    List<java.util.Map<String, Object>> countCreatedByYearForAdvisor(Integer advisorId);
    
    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE c.club_name LIKE CONCAT('%',#{keyword},'%') ORDER BY c.create_time DESC, c.club_id DESC LIMIT #{offset}, #{limit}")
    List<Club> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM clubs WHERE club_name LIKE CONCAT('%',#{keyword},'%')")
    int searchCount(String keyword);

    @Select("SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE c.status = #{status} AND c.club_name LIKE CONCAT('%',#{keyword},'%') ORDER BY c.create_time DESC, c.club_id DESC LIMIT #{offset}, #{limit}")
    List<Club> searchByStatus(@Param("keyword") String keyword, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM clubs WHERE status = #{status} AND club_name LIKE CONCAT('%',#{keyword},'%')")
    int searchCountByStatus(@Param("keyword") String keyword, @Param("status") String status);

    @Select("<script>" +
            "SELECT c.*, u.real_name as founder_name, t.real_name as advisor_name " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.founder_id = u.user_id " +
            "LEFT JOIN users t ON c.advisor_id = t.user_id " +
            "WHERE (" +
            "<if test='includeAdvisor'>c.advisor_id = #{userId} OR </if>" +
            "c.club_id = #{clubId} " +
            "OR EXISTS (SELECT 1 FROM club_members cm WHERE cm.club_id = c.club_id AND cm.user_id = #{userId} AND cm.role IN ('leader','club_leader') AND cm.status = 'active')) " +
            "AND c.club_name LIKE CONCAT('%',#{keyword},'%') ORDER BY c.create_time DESC, c.club_id DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Club> searchManageable(@Param("keyword") String keyword, @Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("includeAdvisor") boolean includeAdvisor, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM clubs c WHERE (" +
            "<if test='includeAdvisor'>c.advisor_id = #{userId} OR </if>" +
            "c.club_id = #{clubId} " +
            "OR EXISTS (SELECT 1 FROM club_members cm WHERE cm.club_id = c.club_id AND cm.user_id = #{userId} AND cm.role IN ('leader','club_leader') AND cm.status = 'active')) " +
            "AND c.club_name LIKE CONCAT('%',#{keyword},'%')" +
            "</script>")
    int searchManageableCount(@Param("keyword") String keyword, @Param("userId") Integer userId, @Param("clubId") Integer clubId, @Param("includeAdvisor") boolean includeAdvisor);
}
