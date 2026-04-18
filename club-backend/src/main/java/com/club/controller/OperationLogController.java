package com.club.controller;

import com.club.common.Result;
import com.club.entity.OperationLog;
import com.club.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class OperationLogController {
    
    private final OperationLogService operationLogService;
    
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String status) {
        if (module != null || operator != null || status != null) {
            return Result.ok(operationLogService.search(module, operator, status, page, size));
        }
        return Result.ok(operationLogService.list(page, size));
    }
    
    @GetMapping("/{id}")
    public Result<OperationLog> getById(@PathVariable Integer id) {
        return Result.ok(operationLogService.getById(id));
    }
    
    @PostMapping
    public Result<OperationLog> create(@RequestBody OperationLog log) {
        return Result.ok(operationLogService.create(log));
    }
}