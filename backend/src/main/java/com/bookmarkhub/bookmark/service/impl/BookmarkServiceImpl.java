package com.bookmarkhub.bookmark.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmarkhub.auth.entity.UserAccount;
import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.auth.service.AuthService;
import com.bookmarkhub.auth.service.UserAccountService;
import com.bookmarkhub.bookmark.dto.SaveBookmarkRequest;
import com.bookmarkhub.bookmark.entity.Bookmark;
import com.bookmarkhub.bookmark.mapper.BookmarkMapper;
import com.bookmarkhub.bookmark.service.BookmarkService;
import com.bookmarkhub.bookmark.vo.BookmarkDetailVO;
import com.bookmarkhub.bookmark.vo.BookmarkSummaryVO;
import com.bookmarkhub.category.entity.Category;
import com.bookmarkhub.category.service.CategoryService;
import com.bookmarkhub.operationlog.enums.OperationAction;
import com.bookmarkhub.operationlog.service.OperationLogService;
import com.bookmarkhub.shared.BizException;
import com.bookmarkhub.shared.ErrorCode;
import com.bookmarkhub.shared.PageResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl extends ServiceImpl<BookmarkMapper, Bookmark> implements BookmarkService {

    private final CategoryService categoryService;
    private final UserAccountService userAccountService;
    private final AuthService authService;
    private final OperationLogService operationLogService;

    @Override
    public PageResponse<BookmarkSummaryVO> list(String username) {
        AuthActor actor = authService.requireActor(username);
        List<Bookmark> bookmarks = this.lambdaQuery()
                .eq(Bookmark::getTeamId, actor.teamId())
                .orderByAsc(Bookmark::getId)
                .list();
        if (bookmarks.isEmpty()) {
            return new PageResponse<>(List.of());
        }

        // 一次批量查出创建人，避免每条书签都 getById 造成 N+1
        Map<Long, String> creatorNamesById = loadCreatorNames(bookmarks);
        List<BookmarkSummaryVO> items = bookmarks.stream()
                .map(bookmark -> new BookmarkSummaryVO(
                        bookmark.getId(),
                        bookmark.getTitle(),
                        bookmark.getUrl(),
                        creatorNamesById.get(bookmark.getCreatedBy())
                ))
                .toList();
        return new PageResponse<>(items);
    }

    @Override
    public BookmarkDetailVO detail(String username, Long bookmarkId) {
        AuthActor actor = authService.requireActor(username);
        Bookmark bookmark = findByIdAndTeamId(bookmarkId, actor.teamId())
                .orElseThrow(() -> new BizException(ErrorCode.BOOKMARK_NOT_FOUND));
        return toDetail(bookmark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookmarkDetailVO create(String username, SaveBookmarkRequest request) {
        AuthActor actor = authService.requireActor(username);
        Category category = requireCategory(actor.teamId(), request.getCategoryId());

        Bookmark bookmark = new Bookmark();
        bookmark.setTeamId(actor.teamId());
        bookmark.setCategoryId(category.getId());
        bookmark.setTitle(request.getTitle());
        bookmark.setUrl(request.getUrl());
        bookmark.setDescription(request.getDescription());
        bookmark.setCreatedBy(actor.userId());
        this.save(bookmark);
        operationLogService.record(actor, OperationAction.CREATE, bookmark, category, null);
        return toDetail(bookmark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookmarkDetailVO update(String username, Long bookmarkId, SaveBookmarkRequest request) {
        AuthActor actor = authService.requireActor(username);
        Bookmark bookmark = findByIdAndTeamId(bookmarkId, actor.teamId())
                .orElseThrow(() -> new BizException(ErrorCode.BOOKMARK_NOT_FOUND));
        if (!actor.isAdmin() && !bookmark.getCreatedBy().equals(actor.userId())) {
            throw new AccessDeniedException("Only owner or admin can modify bookmark");
        }

        Category category = requireCategory(actor.teamId(), request.getCategoryId());
        bookmark.setCategoryId(category.getId());
        bookmark.setTitle(request.getTitle());
        bookmark.setUrl(request.getUrl());
        bookmark.setDescription(request.getDescription());
        this.updateById(bookmark);
        operationLogService.record(actor, OperationAction.UPDATE, bookmark, category, null);
        return toDetail(bookmark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String username, Long bookmarkId) {
        AuthActor actor = authService.requireActor(username);
        Bookmark bookmark = findByIdAndTeamId(bookmarkId, actor.teamId())
                .orElseThrow(() -> new BizException(ErrorCode.BOOKMARK_NOT_FOUND));
        if (!actor.isAdmin() && !bookmark.getCreatedBy().equals(actor.userId())) {
            throw new AccessDeniedException("Only owner or admin can delete bookmark");
        }
        Category category = categoryService.findByIdAndTeamId(bookmark.getCategoryId(), actor.teamId()).orElse(null);
        this.removeById(bookmarkId);
        operationLogService.record(actor, OperationAction.DELETE, bookmark, category, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveImported(List<Bookmark> bookmarks) {
        this.saveBatch(bookmarks);
    }

    @Override
    public long countByTeamId(Long teamId) {
        return this.lambdaQuery()
                .eq(Bookmark::getTeamId, teamId)
                .count();
    }

    private Category requireCategory(Long teamId, Long categoryId) {
        return categoryService.findByIdAndTeamId(categoryId, teamId)
                .orElseThrow(() -> new BizException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Map<Long, String> loadCreatorNames(List<Bookmark> bookmarks) {
        List<Long> creatorIds = bookmarks.stream()
                .map(Bookmark::getCreatedBy)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (creatorIds.isEmpty()) {
            return Map.of();
        }
        return userAccountService.listByIds(creatorIds).stream()
                .collect(Collectors.toMap(UserAccount::getId, UserAccount::getDisplayName));
    }

    private BookmarkDetailVO toDetail(Bookmark bookmark) {
        UserAccount creator = Optional.ofNullable(userAccountService.getById(bookmark.getCreatedBy()))
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "创建人不存在"));
        return new BookmarkDetailVO(
                bookmark.getId(),
                bookmark.getTitle(),
                bookmark.getUrl(),
                bookmark.getDescription(),
                bookmark.getCategoryId(),
                creator.getDisplayName()
        );
    }

    private Optional<Bookmark> findByIdAndTeamId(Long bookmarkId, Long teamId) {
        // id 是主键，命中至多一行，无需再拼 last("LIMIT 1")
        return Optional.ofNullable(this.lambdaQuery()
                .eq(Bookmark::getId, bookmarkId)
                .eq(Bookmark::getTeamId, teamId)
                .one());
    }
}
