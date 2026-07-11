package com.bookmarkhub.operationlog.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogVO {

    private Long id;
    private String action;
    private String operatorName;
    private String operatorRole;
    private String bookmarkTitle;
    private String categoryName;
    private LocalDateTime createdAt;
}
