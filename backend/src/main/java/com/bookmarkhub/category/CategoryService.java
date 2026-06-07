package com.bookmarkhub.category;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bookmarkhub.auth.AuthActor;
import com.bookmarkhub.auth.AuthService;
import com.bookmarkhub.shared.PageResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final AuthService authService;

    public CategoryService(CategoryMapper categoryMapper, AuthService authService) {
        this.categoryMapper = categoryMapper;
        this.authService = authService;
    }

    public CategoryResponse create(String username, SaveCategoryRequest request) {
        AuthActor actor = authService.requireActor(username);
        if (!actor.isAdmin()) {
            throw new AccessDeniedException("Only admin can create category");
        }
        if (request.parentId() != null) {
            findByIdAndTeamId(request.parentId(), actor.teamId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent category not found"));
        }

        Category category = new Category();
        category.setTeamId(actor.teamId());
        category.setParentId(request.parentId());
        category.setName(request.name());
        category.setCreatedBy(actor.userId());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.insert(category);
        return toResponse(category);
    }

    public PageResponse<CategoryResponse> list(String username) {
        AuthActor actor = authService.requireActor(username);
        List<CategoryResponse> items = categoryMapper.selectList(Wrappers.<Category>lambdaQuery()
                        .eq(Category::getTeamId, actor.teamId())
                        .orderByAsc(Category::getId))
                .stream()
                .map(this::toResponse)
                .toList();
        return new PageResponse<>(items);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getParentId());
    }

    private Optional<Category> findByIdAndTeamId(Long categoryId, Long teamId) {
        return Optional.ofNullable(categoryMapper.selectOne(Wrappers.<Category>lambdaQuery()
                .eq(Category::getId, categoryId)
                .eq(Category::getTeamId, teamId)
                .last("LIMIT 1")));
    }
}

record SaveCategoryRequest(@NotBlank String name, Long parentId) {
}

record CategoryResponse(Long id, String name, Long parentId) {
}
