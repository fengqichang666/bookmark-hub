package com.bookmarkhub.bookmark.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveBookmarkRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String url;

    private String description;

    @NotNull
    private Long categoryId;
}
