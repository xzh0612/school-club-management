package com.club.service.impl;

import com.club.entity.OperationLog;
import com.club.mapper.OperationLogMapper;
import com.club.service.OperationLogService;
import com.club.common.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {
    
    private final OperationLogMapper operationLogMapper;
    
    @Override
    public PageResult<OperationLog> list(int page, int size) {
        int offset = (page - 1) * size;
        List<OperationLog> logs = operationLogMapper.findAll(offset, size);
        int total = operationLogMapper.countAll();
        return PageResult.of(logs, total, page, size);
    }
    
    @Override
    public OperationLog getById(Integer id) {
        return operationLogMapper.findById(id);
    }
    
    @Override
    public OperationLog create(OperationLog log) {
        log.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
        return log;
    }
    
    @Override
    public PageResult<OperationLog> search(String module, String operator, String status, int page, int size) {
        int offset = (page - 1) * size;
        List<OperationLog> logs;
        int total;
        
        if (module != null && !module.isEmpty()) {
            logs = operationLogMapper.findByModule(module, offset, size);
            total = operationLogMapper.countByModule(module);
        } else if (operator != null && !operator.isEmpty()) {
            logs = operationLogMapper.findByOperator(operator, offset, size);
            total = operationLogMapper.countByOperator(operator);
        } else if (status != null && !status.isEmpty()) {
            logs = operationLogMapper.findByStatus(status, offset, size);
            total = operationLogMapper.countByStatus(status);
        } else {
            return list(page, size);
        }
        
        return PageResult.of(logs, total, page, size);
    }
}