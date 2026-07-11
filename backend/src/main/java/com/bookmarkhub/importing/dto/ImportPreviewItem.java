package com.bookmarkhub.importing.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导入条目。同时用于 parse 出参和 confirm 入参：
 * - 出参场景：由后端解析 HTML 后返回
 * - 入参场景：前端把确认后的列表回传给 confirm 接口
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportPreviewItem {

    @NotBlank
    private String title;

    @NotBlank
    private String url;

    private String folderPath;
}
