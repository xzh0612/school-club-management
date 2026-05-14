package com.club.mapper;

import com.club.entity.Approval;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ApprovalMapper {
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "WHERE ap.approval_id = #{approvalId}")
    Approval findById(Integer approvalId);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "ORDER BY ap.create_time DESC, ap.approval_id DESC LIMIT #{offset}, #{limit}")
    List<Approval> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals")
    int countAll();

    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "WHERE ap.status = #{status} ORDER BY ap.create_time DESC, ap.approval_id DESC LIMIT #{offset}, #{limit}")
    List<Approval> findByStatus(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM approvals WHERE status = #{status}")
    int countByStatus(String status);

    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "WHERE ap.type = #{type} AND ap.status = #{status} ORDER BY ap.create_time DESC, ap.approval_id DESC LIMIT #{offset}, #{limit}")
    List<Approval> findByTypeAndStatus(@Param("type") String type, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Insert("INSERT INTO approvals(type, related_id, applicant_id, approver_id, status, comments, current_step, total_steps) VALUES(#{type}, #{relatedId}, #{applicantId}, #{approverId}, #{status}, #{comments}, #{currentStep}, #{totalSteps})")
    @Options(useGeneratedKeys = true, keyProperty = "approvalId")
    int insert(Approval approval);
    
    @Update("UPDATE approvals SET type = #{type}, related_id = #{relatedId}, applicant_id = #{applicantId}, approver_id = #{approverId}, status = #{status}, comments = #{comments}, current_step = #{currentStep}, total_steps = #{totalSteps}, update_time = NOW() WHERE approval_id = #{approvalId}")
    int update(Approval approval);
    
    @Update("UPDATE approvals SET status = #{status}, approver_id = #{approverId}, comments = #{comments}, approval_time = NOW(), update_time = NOW() WHERE approval_id = #{approvalId}")
    int updateStatus(Approval approval);

    @Update("UPDATE approvals SET current_step = #{currentStep}, approver_id = #{approverId}, comments = #{comments}, update_time = NOW() WHERE approval_id = #{approvalId}")
    int updateStep(Approval approval);
    
    @Update("UPDATE approvals SET status = 'archived', update_time = NOW() WHERE approval_id = #{approvalId}")
    int deleteById(Integer approvalId);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "WHERE ap.type = #{type} ORDER BY ap.create_time DESC, ap.approval_id DESC LIMIT #{offset}, #{limit}")
    List<Approval> findByType(@Param("type") String type, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE type = #{type}")
    int countByType(String type);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "WHERE ap.status = 'pending' ORDER BY ap.create_time DESC, ap.approval_id DESC LIMIT #{offset}, #{limit}")
    List<Approval> findPending(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE status = 'pending'")
    int countPending();

    @Select("SELECT COUNT(*) FROM approvals WHERE type = #{type} AND related_id = #{relatedId} AND status = 'pending'")
    int countPendingByTypeAndRelated(@Param("type") String type, @Param("relatedId") Integer relatedId);

    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "WHERE (((ap.type = 'club_creation' AND EXISTS (SELECT 1 FROM clubs c1 WHERE c1.club_id = ap.related_id AND c1.advisor_id = #{advisorId})) " +
            "OR (ap.type = 'activity_application' AND EXISTS (SELECT 1 FROM activities a1 INNER JOIN clubs c1 ON a1.club_id = c1.club_id WHERE a1.activity_id = ap.related_id AND c1.advisor_id = #{advisorId})))) " +
            "AND (#{type} IS NULL OR #{type} = '' OR ap.type = #{type}) " +
            "AND (#{status} IS NULL OR #{status} = '' OR ap.status = #{status}) " +
            "ORDER BY ap.create_time DESC, ap.approval_id DESC LIMIT #{offset}, #{limit}")
    List<Approval> findVisibleToAdvisor(
            @Param("advisorId") Integer advisorId,
            @Param("type") String type,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM approvals ap " +
            "WHERE (((ap.type = 'club_creation' AND EXISTS (SELECT 1 FROM clubs c1 WHERE c1.club_id = ap.related_id AND c1.advisor_id = #{advisorId})) " +
            "OR (ap.type = 'activity_application' AND EXISTS (SELECT 1 FROM activities a1 INNER JOIN clubs c1 ON a1.club_id = c1.club_id WHERE a1.activity_id = ap.related_id AND c1.advisor_id = #{advisorId})))) " +
            "AND (#{type} IS NULL OR #{type} = '' OR ap.type = #{type}) " +
            "AND (#{status} IS NULL OR #{status} = '' OR ap.status = #{status})")
    int countVisibleToAdvisor(
            @Param("advisorId") Integer advisorId,
            @Param("type") String type,
            @Param("status") String status);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "WHERE ap.approver_id = #{approverId} ORDER BY ap.create_time DESC, ap.approval_id DESC LIMIT #{offset}, #{limit}")
    List<Approval> findByApproverId(@Param("approverId") Integer approverId, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE approver_id = #{approverId}")
    int countByApproverId(Integer approverId);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "WHERE ap.applicant_id = #{applicantId} ORDER BY ap.create_time DESC, ap.approval_id DESC LIMIT #{offset}, #{limit}")
    List<Approval> findByApplicantId(@Param("applicantId") Integer applicantId, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE applicant_id = #{applicantId}")
    int countByApplicantId(Integer applicantId);
    
    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "WHERE ap.type LIKE CONCAT('%',#{keyword},'%') OR ap.comments LIKE CONCAT('%',#{keyword},'%') ORDER BY ap.create_time DESC, ap.approval_id DESC LIMIT #{offset}, #{limit}")
    List<Approval> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE type LIKE CONCAT('%',#{keyword},'%') OR comments LIKE CONCAT('%',#{keyword},'%')")
    int searchCount(String keyword);

    @Select("SELECT ap.*, u.real_name as applicant_name, au.real_name as approver_name, " +
            "CASE ap.type WHEN 'club_creation' THEN c.club_name WHEN 'activity_application' THEN a.title END as related_title " +
            "FROM approvals ap " +
            "LEFT JOIN users u ON ap.applicant_id = u.user_id " +
            "LEFT JOIN users au ON ap.approver_id = au.user_id " +
            "LEFT JOIN clubs c ON ap.type = 'club_creation' AND ap.related_id = c.club_id " +
            "LEFT JOIN activities a ON ap.type = 'activity_application' AND ap.related_id = a.activity_id " +
            "WHERE (((ap.type = 'club_creation' AND EXISTS (SELECT 1 FROM clubs c1 WHERE c1.club_id = ap.related_id AND c1.advisor_id = #{advisorId})) " +
            "OR (ap.type = 'activity_application' AND EXISTS (SELECT 1 FROM activities a1 INNER JOIN clubs c1 ON a1.club_id = c1.club_id WHERE a1.activity_id = ap.related_id AND c1.advisor_id = #{advisorId})))) " +
            "AND (ap.type LIKE CONCAT('%',#{keyword},'%') OR ap.comments LIKE CONCAT('%',#{keyword},'%')) " +
            "ORDER BY ap.create_time DESC, ap.approval_id DESC LIMIT #{offset}, #{limit}")
    List<Approval> searchVisibleToAdvisor(
            @Param("advisorId") Integer advisorId,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM approvals ap " +
            "WHERE (((ap.type = 'club_creation' AND EXISTS (SELECT 1 FROM clubs c1 WHERE c1.club_id = ap.related_id AND c1.advisor_id = #{advisorId})) " +
            "OR (ap.type = 'activity_application' AND EXISTS (SELECT 1 FROM activities a1 INNER JOIN clubs c1 ON a1.club_id = c1.club_id WHERE a1.activity_id = ap.related_id AND c1.advisor_id = #{advisorId})))) " +
            "AND (ap.type LIKE CONCAT('%',#{keyword},'%') OR ap.comments LIKE CONCAT('%',#{keyword},'%'))")
    int searchCountVisibleToAdvisor(@Param("advisorId") Integer advisorId, @Param("keyword") String keyword);
    
    @Select("SELECT COUNT(*) FROM approvals WHERE type = #{type} AND status = #{status}")
    int countByTypeAndStatus(@Param("type") String type, @Param("status") String status);
}
