package com.bookmarkhub.importing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmarkhub.importing.entity.ImportRecord;
import com.bookmarkhub.importing.mapper.ImportRecordMapper;
import com.bookmarkhub.importing.service.ImportRecordService;
import org.springframework.stereotype.Service;

@Service
public class ImportRecordServiceImpl extends ServiceImpl<ImportRecordMapper, ImportRecord> implements ImportRecordService {
}
