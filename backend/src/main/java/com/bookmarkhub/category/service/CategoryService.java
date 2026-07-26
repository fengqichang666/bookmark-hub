package com.bookmarkhub.category.service;

import com.bookmarkhub.category.dto.SaveCategoryRequest;
import com.bookmarkhub.category.entity.Category;
import com.bookmarkhub.category.vo.CategoryVO;
import com.bookmarkhub.shared.PageResponse;
import java.util.Optional;

/**
 * 分类业务接口。不继承 IService，理由同 {@link com.bookmarkhub.bookmark.service.BookmarkService}。
 */
public interface CategoryService {

    CategoryVO create(String username, SaveCategoryRequest request);

    PageResponse<CategoryVO> list(String username);

    /** 跨模块使用：按 id + teamId 精确定位分类。 */
    Optional<Category> findByIdAndTeamId(Long categoryId, Long teamId);

    /** 供 dashboard 统计团队分类数。 */
    long countByTeamId(Long teamId);
}
