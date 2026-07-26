package com.bookmarkhub.operationlog.service;

import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.bookmark.entity.Bookmark;
import com.bookmarkhub.category.entity.Category;
import com.bookmarkhub.operationlog.dto.OperationLogQuery;
import com.bookmarkhub.operationlog.enums.OperationAction;
import com.bookmarkhub.operationlog.vo.OperationLogVO;
import com.bookmarkhub.shared.PageResult;

/**
 * 操作日志业务接口。不继承 IService，避免调用方绕过 team 过滤直接读写日志表。
 */
public interface OperationLogService {

    void record(AuthActor actor, OperationAction action, Bookmark bookmark, Category category, String detail);

    PageResult<OperationLogVO> list(String username, OperationLogQuery query);
}
