package com.club.controller;

import com.club.common.Result;
import com.club.common.SecurityContext;
import com.club.entity.OperationLog;
import com.club.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class OperationLogController {
    
    private final OperationLogService operationLogService;
    private final SecurityContext securityContext;
    
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        securityContext.requireAdmin(request);
        if (module != null || operator != null || status != null) {
            return Result.ok(operationLogService.search(module, operator, status, page, size));
        }
        return Result.ok(operationLogService.list(page, size));
    }
    
    @GetMapping("/{id}")
    public Result<OperationLog> getById(@PathVariable Integer id, HttpServletRequest request) {
        securityContext.requireAdmin(request);
        return Result.ok(operationLogService.getById(id));
    }
    
}
