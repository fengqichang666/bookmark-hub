package com.bookmarkhub.category.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.auth.service.AuthService;
import com.bookmarkhub.category.dto.SaveCategoryRequest;
import com.bookmarkhub.category.entity.Category;
import com.bookmarkhub.category.mapper.CategoryMapper;
import com.bookmarkhub.category.service.CategoryService;
import com.bookmarkhub.category.vo.CategoryVO;
import com.bookmarkhub.shared.BizException;
import com.bookmarkhub.shared.ErrorCode;
import com.bookmarkhub.shared.PageResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final AuthService authService;

    @Override
    public CategoryVO create(String username, SaveCategoryRequest request) {
        AuthActor actor = authService.requireActor(username);
        if (request.getParentId() != null) {
            findByIdAndTeamId(request.getParentId(), actor.teamId())
                    .orElseThrow(() -> new BizException(ErrorCode.CATEGORY_NOT_FOUND, "父分类不存在"));
        }

        Category category = new Category();
        category.setTeamId(actor.teamId());
        category.setParentId(request.getParentId());
        category.setName(request.getName());
        category.setCreatedBy(actor.userId());
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
        // id 是主键，命中至多一行，无需再拼 last("LIMIT 1")
        return Optional.ofNullable(this.lambdaQuery()
                .eq(Category::getId, categoryId)
                .eq(Category::getTeamId, teamId)
                .one());
    }

    @Override
    public long countByTeamId(Long teamId) {
        return this.lambdaQuery()
                .eq(Category::getTeamId, teamId)
                .count();
    }

    private CategoryVO toVO(Category category) {
        return new CategoryVO(category.getId(), category.getName(), category.getParentId());
    }
}
