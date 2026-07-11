package com.bookmarkhub.category.controller;

import com.bookmarkhub.category.dto.SaveCategoryRequest;
import com.bookmarkhub.category.service.CategoryService;
import com.bookmarkhub.category.vo.CategoryVO;
import com.bookmarkhub.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "List categories")
    public PageResponse<CategoryVO> list(Authentication authentication) {
        return categoryService.list(authentication.getName());
    }

    @PostMapping
    @Operation(summary = "Create category")
    public CategoryVO create(@Valid @RequestBody SaveCategoryRequest request, Authentication authentication) {
        return categoryService.create(authentication.getName(), request);
    }
}
