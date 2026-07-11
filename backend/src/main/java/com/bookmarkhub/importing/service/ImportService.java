package com.bookmarkhub.importing.service;

import com.bookmarkhub.importing.dto.ConfirmImportRequest;
import com.bookmarkhub.importing.vo.ImportPreviewVO;
import com.bookmarkhub.importing.vo.ImportResultVO;
import org.springframework.web.multipart.MultipartFile;

public interface ImportService {

    ImportPreviewVO parse(String username, MultipartFile file);

    ImportResultVO confirm(String username, ConfirmImportRequest request);
}
