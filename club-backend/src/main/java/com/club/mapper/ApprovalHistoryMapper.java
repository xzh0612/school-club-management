package com.club.mapper;

import com.club.entity.ApprovalHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApprovalHistoryMapper {

    @Insert("INSERT INTO approval_histories(approval_id, step_no, operator_id, action, comments) " +
            "VALUES(#{approvalId}, #{stepNo}, #{operatorId}, #{action}, #{comments})")
    @Options(useGeneratedKeys = true, keyProperty = "historyId")
    int insert(ApprovalHistory history);

    @Select("SELECT * FROM approval_histories WHERE approval_id = #{approvalId} ORDER BY create_time ASC, history_id ASC")
    List<ApprovalHistory> findByApprovalId(Integer approvalId);
}
