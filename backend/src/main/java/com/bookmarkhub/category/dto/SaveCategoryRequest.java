package com.bookmarkhub.category.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveCategoryRequest {

    @NotBlank
    private String name;

    private Long parentId;
}
