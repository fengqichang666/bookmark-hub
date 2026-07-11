package com.bookmarkhub.operationlog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long operatorId;
    private String operatorName;
    private String operatorRole;
    private Long teamId;
    private Long bookmarkId;
    private String bookmarkTitle;
    private Long categoryId;
    private String categoryName;
    private String action;
    private String detail;
    private LocalDateTime createdAt;
}
