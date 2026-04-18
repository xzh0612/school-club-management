package com.club.mapper;

import com.club.entity.OperationLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OperationLogMapper {
    
    @Select("SELECT * FROM operation_logs ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<OperationLog> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM operation_logs")
    int countAll();
    
    @Insert("INSERT INTO operation_logs(operator, module, action, description, ip, user_agent, status) VALUES(#{operator}, #{module}, #{action}, #{description}, #{ip}, #{userAgent}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);
    
    @Select("SELECT * FROM operation_logs WHERE id = #{id}")
    OperationLog findById(Integer id);
    
    @Select("SELECT * FROM operation_logs WHERE module = #{module} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<OperationLog> findByModule(@Param("module") String module, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM operation_logs WHERE module = #{module}")
    int countByModule(String module);
    
    @Select("SELECT * FROM operation_logs WHERE operator = #{operator} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<OperationLog> findByOperator(@Param("operator") String operator, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM operation_logs WHERE operator = #{operator}")
    int countByOperator(String operator);
    
    @Select("SELECT * FROM operation_logs WHERE status = #{status} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<OperationLog> findByStatus(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM operation_logs WHERE status = #{status}")
    int countByStatus(String status);
}