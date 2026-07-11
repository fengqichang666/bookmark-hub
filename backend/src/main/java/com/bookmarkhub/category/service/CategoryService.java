package com.bookmarkhub.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmarkhub.category.dto.SaveCategoryRequest;
import com.bookmarkhub.category.entity.Category;
import com.bookmarkhub.category.vo.CategoryVO;
import com.bookmarkhub.shared.PageResponse;
import java.util.Optional;

public interface CategoryService extends IService<Category> {

    CategoryVO create(String username, SaveCategoryRequest request);

    PageResponse<CategoryVO> list(String username);

    /** 跨模块使用：按 id + teamId 精确定位分类。 */
    Optional<Category> findByIdAndTeamId(Long categoryId, Long teamId);
}
