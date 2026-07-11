package com.bookmarkhub.category.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.auth.service.AuthService;
import com.bookmarkhub.category.dto.SaveCategoryRequest;
import com.bookmarkhub.category.entity.Category;
import com.bookmarkhub.category.mapper.CategoryMapper;
import com.bookmarkhub.category.service.CategoryService;
import com.bookmarkhub.category.vo.CategoryVO;
import com.bookmarkhub.shared.PageResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final AuthService authService;

    @Override
    public CategoryVO create(String username, SaveCategoryRequest request) {
        AuthActor actor = authService.requireActor(username);
        if (!actor.isAdmin()) {
            throw new AccessDeniedException("Only admin can create category");
        }
        if (request.getParentId() != null) {
            findByIdAndTeamId(request.getParentId(), actor.teamId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent category not found"));
        }

        Category category = new Category();
        category.setTeamId(actor.teamId());
        category.setParentId(request.getParentId());
        category.setName(request.getName());
        category.setCreatedBy(actor.userId());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        this.save(category);
        return toVO(category);
    }

    @Override
    public PageResponse<CategoryVO> list(String username) {
        AuthActor actor = authService.requireActor(username);
        List<CategoryVO> items = this.lambdaQuery()
                .eq(Category::getTeamId, actor.teamId())
                .orderByAsc(Category::getId)
                .list()
                .stream()
                .map(this::toVO)
                .toList();
        return new PageResponse<>(items);
    }

    @Override
    public Optional<Category> findByIdAndTeamId(Long categoryId, Long teamId) {
        return this.lambdaQuery()
                .eq(Category::getId, categoryId)
                .eq(Category::getTeamId, teamId)
                .last("LIMIT 1")
                .oneOpt();
    }

    private CategoryVO toVO(Category category) {
        return new CategoryVO(category.getId(), category.getName(), category.getParentId());
    }
}
