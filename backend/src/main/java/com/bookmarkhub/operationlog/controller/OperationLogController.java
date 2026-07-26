package com.bookmarkhub.operationlog.controller;

import com.bookmarkhub.operationlog.dto.OperationLogQuery;
import com.bookmarkhub.operationlog.service.OperationLogService;
import com.bookmarkhub.operationlog.vo.OperationLogVO;
import com.bookmarkhub.shared.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "OperationLogs")
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    @Operation(summary = "分页查询书签操作日志")
    public PageResult<OperationLogVO> list(OperationLogQuery query, Authentication authentication) {
        return operationLogService.list(authentication.getName(), query);
    }
}
