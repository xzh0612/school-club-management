package com.club.mapper;

import com.club.entity.Approval;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ApprovalMapper {
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type " +
            "  WHEN 'club_creation' THEN c.club_name " +
            "  WHEN 'activity_application' THEN a.title " +
            "  WHEN 'recruitment_application' THEN rc.club_name " +
            "END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "LEFT JOIN clubs rc ON ap.type = 'recruitment_application' AND ap.related_id = rc.club_id " +
            "WHERE ap.approval_id = #{approvalId}")
    Approval findById(Integer approvalId);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type " +
            "  WHEN 'club_creation' THEN c.club_name " +
            "  WHEN 'activity_application' THEN a.title " +
            "  WHEN 'recruitment_application' THEN rc.club_name " +
            "END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "LEFT JOIN clubs rc ON ap.type = 'recruitment_application' AND ap.related_id = rc.club_id " +
            "WHERE ap.type <> 'recruitment_application' LIMIT #{offset}, #{limit}")
    List<Approval> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE type <> 'recruitment_application'")
    int countAll();

    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type " +
            "  WHEN 'club_creation' THEN c.club_name " +
            "  WHEN 'activity_application' THEN a.title " +
            "  WHEN 'recruitment_application' THEN rc.club_name " +
            "END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "LEFT JOIN clubs rc ON ap.type = 'recruitment_application' AND ap.related_id = rc.club_id " +
            "WHERE ap.status = #{status} AND ap.type <> 'recruitment_application' LIMIT #{offset}, #{limit}")
    List<Approval> findByStatus(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM approvals WHERE status = #{status} AND type <> 'recruitment_application'")
    int countByStatus(String status);

    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type " +
            "  WHEN 'club_creation' THEN c.club_name " +
            "  WHEN 'activity_application' THEN a.title " +
            "  WHEN 'recruitment_application' THEN rc.club_name " +
            "END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "LEFT JOIN clubs rc ON ap.type = 'recruitment_application' AND ap.related_id = rc.club_id " +
            "WHERE ap.type = #{type} AND ap.status = #{status} LIMIT #{offset}, #{limit}")
    List<Approval> findByTypeAndStatus(@Param("type") String type, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Insert("INSERT INTO approvals(type, related_id, applicant_id, approver_id, status, comments, current_step, total_steps) VALUES(#{type}, #{relatedId}, #{applicantId}, #{approverId}, #{status}, #{comments}, #{currentStep}, #{totalSteps})")
    @Options(useGeneratedKeys = true, keyProperty = "approvalId")
    int insert(Approval approval);
    
    @Update("UPDATE approvals SET type = #{type}, related_id = #{relatedId}, applicant_id = #{applicantId}, approver_id = #{approverId}, status = #{status}, comments = #{comments}, current_step = #{currentStep}, total_steps = #{totalSteps}, update_time = NOW() WHERE approval_id = #{approvalId}")
    int update(Approval approval);
    
    @Update("UPDATE approvals SET status = #{status}, approver_id = #{approverId}, comments = #{comments}, approval_time = NOW(), update_time = NOW() WHERE approval_id = #{approvalId}")
    int updateStatus(Approval approval);
    
    @Delete("DELETE FROM approvals WHERE approval_id = #{approvalId}")
    int deleteById(Integer approvalId);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type " +
            "  WHEN 'club_creation' THEN c.club_name " +
            "  WHEN 'activity_application' THEN a.title " +
            "  WHEN 'recruitment_application' THEN rc.club_name " +
            "END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "LEFT JOIN clubs rc ON ap.type = 'recruitment_application' AND ap.related_id = rc.club_id " +
            "WHERE ap.type = #{type} LIMIT #{offset}, #{limit}")
    List<Approval> findByType(@Param("type") String type, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE type = #{type}")
    int countByType(String type);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type " +
            "  WHEN 'club_creation' THEN c.club_name " +
            "  WHEN 'activity_application' THEN a.title " +
            "  WHEN 'recruitment_application' THEN rc.club_name " +
            "END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "LEFT JOIN clubs rc ON ap.type = 'recruitment_application' AND ap.related_id = rc.club_id " +
            "WHERE ap.status = 'pending' AND ap.type <> 'recruitment_application' LIMIT #{offset}, #{limit}")
    List<Approval> findPending(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE status = 'pending' AND type <> 'recruitment_application'")
    int countPending();
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type " +
            "  WHEN 'club_creation' THEN c.club_name " +
            "  WHEN 'activity_application' THEN a.title " +
            "  WHEN 'recruitment_application' THEN rc.club_name " +
            "END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "LEFT JOIN clubs rc ON ap.type = 'recruitment_application' AND ap.related_id = rc.club_id " +
            "WHERE ap.approver_id = #{approverId} LIMIT #{offset}, #{limit}")
    List<Approval> findByApproverId(@Param("approverId") Integer approverId, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE approver_id = #{approverId}")
    int countByApproverId(Integer approverId);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type " +
            "  WHEN 'club_creation' THEN c.club_name " +
            "  WHEN 'activity_application' THEN a.title " +
            "  WHEN 'recruitment_application' THEN rc.club_name " +
            "END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "LEFT JOIN clubs rc ON ap.type = 'recruitment_application' AND ap.related_id = rc.club_id " +
            "WHERE ap.applicant_id = #{applicantId} LIMIT #{offset}, #{limit}")
    List<Approval> findByApplicantId(@Param("applicantId") Integer applicantId, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE applicant_id = #{applicantId}")
    int countByApplicantId(Integer applicantId);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type " +
            "  WHEN 'club_creation' THEN c.club_name " +
            "  WHEN 'activity_application' THEN a.title " +
            "  WHEN 'recruitment_application' THEN rc.club_name " +
            "END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "LEFT JOIN clubs rc ON ap.type = 'recruitment_application' AND ap.related_id = rc.club_id " +
            "WHERE ap.type <> 'recruitment_application' AND (ap.type LIKE CONCAT('%',#{keyword},'%') OR ap.comments LIKE CONCAT('%',#{keyword},'%')) LIMIT #{offset}, #{limit}")
    List<Approval> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE type <> 'recruitment_application' AND (type LIKE CONCAT('%',#{keyword},'%') OR comments LIKE CONCAT('%',#{keyword},'%'))")
    int searchCount(String keyword);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE type = #{type} AND status = #{status}")
    int countByTypeAndStatus(@Param("type") String type, @Param("status") String status);
}
