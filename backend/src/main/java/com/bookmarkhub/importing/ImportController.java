package com.bookmarkhub.importing;

import jakarta.validation.Valid;
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
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping(path = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportPreviewResponse parse(@RequestPart("file") MultipartFile file, Authentication authentication) {
        return importService.parse(authentication.getName(), file);
    }

    @PostMapping("/confirm")
    public ImportResultResponse confirm(
            @Valid @RequestBody ConfirmImportRequest request,
            Authentication authentication
    ) {
        return importService.confirm(authentication.getName(), request);
    }
}
