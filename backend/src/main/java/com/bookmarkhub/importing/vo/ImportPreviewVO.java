package com.bookmarkhub.importing.vo;

import com.bookmarkhub.importing.dto.ImportPreviewItem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportPreviewVO {

    private String fileName;
    private List<ImportPreviewItem> items;
}
