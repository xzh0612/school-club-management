package com.club.service.impl;

import com.club.common.PageQuery;
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
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        List<OperationLog> logs = operationLogMapper.findAll(offset, pageSize);
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
    public void record(String operator, String module, String action, String description, String ip, String userAgent, String status) {
        OperationLog log = new OperationLog();
        log.setOperator(operator == null || operator.isBlank() ? "unknown" : operator);
        log.setModule(module);
        log.setAction(action);
        log.setDescription(description);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setStatus(status == null || status.isBlank() ? "success" : status);
        create(log);
    }
    
    @Override
    public PageResult<OperationLog> search(String module, String operator, String status, int page, int size) {
        int pageSize = PageQuery.normalizeSize(size);
        int offset = PageQuery.offset(page, size);
        List<OperationLog> logs;
        int total;
        
        if (module != null && !module.isEmpty()) {
            logs = operationLogMapper.findByModule(module, offset, pageSize);
            total = operationLogMapper.countByModule(module);
        } else if (operator != null && !operator.isEmpty()) {
            logs = operationLogMapper.findByOperator(operator, offset, pageSize);
            total = operationLogMapper.countByOperator(operator);
        } else if (status != null && !status.isEmpty()) {
            logs = operationLogMapper.findByStatus(status, offset, pageSize);
            total = operationLogMapper.countByStatus(status);
        } else {
            return list(page, size);
        }
        
        return PageResult.of(logs, total, page, size);
    }
}
