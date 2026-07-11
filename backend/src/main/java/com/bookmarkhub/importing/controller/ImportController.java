package com.bookmarkhub.importing.controller;

import com.bookmarkhub.importing.dto.ConfirmImportRequest;
import com.bookmarkhub.importing.service.ImportService;
import com.bookmarkhub.importing.vo.ImportPreviewVO;
import com.bookmarkhub.importing.vo.ImportResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/imports")
@Tag(name = "Imports")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping(path = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Parse bookmark import file")
    public ImportPreviewVO parse(@RequestPart("file") MultipartFile file, Authentication authentication) {
        return importService.parse(authentication.getName(), file);
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm bookmark import")
    public ImportResultVO confirm(
            @Valid @RequestBody ConfirmImportRequest request,
            Authentication authentication
    ) {
        return importService.confirm(authentication.getName(), request);
    }
}
