package com.bookmarkhub.operationlog.dto;

import lombok.Data;

@Data
public class OperationLogQuery {

    private String role;
    private Long operatorId;
    private Long categoryId;
    private Long bookmarkId;
    private Integer page;
    private Integer size;

    public int pageOrDefault() {
        return page == null || page < 1 ? 1 : page;
    }

    public int sizeOrDefault() {
        return size == null || size < 1 ? 20 : Math.min(size, 100);
    }
}
