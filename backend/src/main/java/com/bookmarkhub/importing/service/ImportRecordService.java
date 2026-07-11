package com.bookmarkhub.importing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmarkhub.importing.entity.ImportRecord;

/**
 * ImportRecord 的通用 CRUD Service（IService 提供）。
 * 业务导入逻辑走 {@link ImportService}。
 */
public interface ImportRecordService extends IService<ImportRecord> {
}
