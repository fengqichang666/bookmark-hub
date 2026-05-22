package com.bookmarkhub.category;

import com.bookmarkhub.shared.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public PageResponse<CategoryResponse> list(Authentication authentication) {
        return categoryService.list(authentication.getName());
    }

    @PostMapping
    public CategoryResponse create(@Valid @RequestBody SaveCategoryRequest request, Authentication authentication) {
        return categoryService.create(authentication.getName(), request);
    }
}
