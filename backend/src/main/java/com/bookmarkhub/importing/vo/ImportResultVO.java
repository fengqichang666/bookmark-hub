package com.bookmarkhub.importing.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultVO {

    private int totalCount;
    private int successCount;
    private int failedCount;
}
