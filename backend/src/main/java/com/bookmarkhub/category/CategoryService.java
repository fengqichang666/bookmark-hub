package com.bookmarkhub.category;

import com.bookmarkhub.auth.AuthActor;
import com.bookmarkhub.auth.AuthService;
import com.bookmarkhub.shared.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AuthService authService;

    public CategoryService(CategoryRepository categoryRepository, AuthService authService) {
        this.categoryRepository = categoryRepository;
        this.authService = authService;
    }

    public CategoryResponse create(String username, SaveCategoryRequest request) {
        AuthActor actor = authService.requireActor(username);
        if (!actor.isAdmin()) {
            throw new AccessDeniedException("Only admin can create category");
        }
        if (request.parentId() != null) {
            categoryRepository.findByIdAndTeamId(request.parentId(), actor.teamId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent category not found"));
        }

        Category category = new Category();
        category.setTeamId(actor.teamId());
        category.setParentId(request.parentId());
        category.setName(request.name());
        category.setCreatedBy(actor.userId());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return toResponse(categoryRepository.save(category));
    }

    public PageResponse<CategoryResponse> list(String username) {
        AuthActor actor = authService.requireActor(username);
        List<CategoryResponse> items = categoryRepository.findByTeamIdOrderByIdAsc(actor.teamId())
                .stream()
                .map(this::toResponse)
                .toList();
        return new PageResponse<>(items);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getParentId());
    }
}

record SaveCategoryRequest(@NotBlank String name, Long parentId) {
}

record CategoryResponse(Long id, String name, Long parentId) {
}
