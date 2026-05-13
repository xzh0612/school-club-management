package com.club.service;

import com.club.entity.OperationLog;
import com.club.common.PageResult;
import java.util.List;

public interface OperationLogService {
    PageResult<OperationLog> list(int page, int size);
    OperationLog getById(Integer id);
    OperationLog create(OperationLog log);
    void record(String operator, String module, String action, String description, String ip, String userAgent, String status);
    PageResult<OperationLog> search(String module, String operator, String status, int page, int size);
}
