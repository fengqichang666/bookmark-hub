package com.bookmarkhub.operationlog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.bookmark.entity.Bookmark;
import com.bookmarkhub.category.entity.Category;
import com.bookmarkhub.operationlog.dto.OperationLogQuery;
import com.bookmarkhub.operationlog.entity.OperationLog;
import com.bookmarkhub.operationlog.enums.OperationAction;
import com.bookmarkhub.operationlog.vo.OperationLogVO;

public interface OperationLogService extends IService<OperationLog> {

    void record(AuthActor actor, OperationAction action, Bookmark bookmark, Category category, String detail);

    IPage<OperationLogVO> list(String username, OperationLogQuery query);
}
