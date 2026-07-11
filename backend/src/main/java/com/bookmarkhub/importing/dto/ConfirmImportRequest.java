package com.bookmarkhub.importing.dto;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmImportRequest {

    @NotBlank
    private String fileName;

    @NotNull
    private Long categoryId;

    @NotEmpty
    private List<@Valid ImportPreviewItem> items;
}
