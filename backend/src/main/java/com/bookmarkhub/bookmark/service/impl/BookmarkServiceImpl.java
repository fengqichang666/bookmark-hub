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
public class BookmarkServiceImpl extends ServiceImpl<BookmarkMapper, Bookmark> implements BookmarkService {

    private final CategoryService categoryService;
    private final UserAccountService userAccountService;
    private final AuthService authService;
    private final OperationLogService operationLogService;

    @Override
    public PageResponse<BookmarkSummaryVO> list(String username) {
        AuthActor actor = authService.requireActor(username);
        List<BookmarkSummaryVO> items = this.lambdaQuery()
                .eq(Bookmark::getTeamId, actor.teamId())
                .orderByAsc(Bookmark::getId)
                .list()
                .stream()
                .map(this::toSummary)
                .toList();
        return new PageResponse<>(items);
    }

    @Override
    public BookmarkDetailVO detail(String username, Long bookmarkId) {
        AuthActor actor = authService.requireActor(username);
        Bookmark bookmark = findByIdAndTeamId(bookmarkId, actor.teamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bookmark not found"));
        return toDetail(bookmark);
    }

    @Override
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
        bookmark.setCreatedAt(LocalDateTime.now());
        bookmark.setUpdatedAt(LocalDateTime.now());
        this.save(bookmark);
        operationLogService.record(actor, OperationAction.CREATE, bookmark, category, null);
        return toDetail(bookmark);
    }

    @Override
    public BookmarkDetailVO update(String username, Long bookmarkId, SaveBookmarkRequest request) {
        AuthActor actor = authService.requireActor(username);
        Bookmark bookmark = findByIdAndTeamId(bookmarkId, actor.teamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bookmark not found"));
        if (!actor.isAdmin() && !bookmark.getCreatedBy().equals(actor.userId())) {
            throw new AccessDeniedException("Only owner or admin can modify bookmark");
        }

        Category category = requireCategory(actor.teamId(), request.getCategoryId());
        bookmark.setCategoryId(category.getId());
        bookmark.setTitle(request.getTitle());
        bookmark.setUrl(request.getUrl());
        bookmark.setDescription(request.getDescription());
        bookmark.setUpdatedAt(LocalDateTime.now());
        this.updateById(bookmark);
        operationLogService.record(actor, OperationAction.UPDATE, bookmark, category, null);
        return toDetail(bookmark);
    }

    @Override
    public void delete(String username, Long bookmarkId) {
        AuthActor actor = authService.requireActor(username);
        Bookmark bookmark = findByIdAndTeamId(bookmarkId, actor.teamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "不存在"));
        if (!actor.isAdmin() && !bookmark.getCreatedBy().equals(actor.userId())) {
            throw new AccessDeniedException("Only owner or admin can delete bookmark");
        }
        Category category = categoryService.findByIdAndTeamId(bookmark.getCategoryId(), actor.teamId()).orElse(null);
        this.removeById(bookmarkId);
        operationLogService.record(actor, OperationAction.DELETE, bookmark, category, null);
    }

    private Category requireCategory(Long teamId, Long categoryId) {
        return categoryService.findByIdAndTeamId(categoryId, teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private BookmarkSummaryVO toSummary(Bookmark bookmark) {
        UserAccount creator = Optional.ofNullable(userAccountService.getById(bookmark.getCreatedBy()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator not found"));
        return new BookmarkSummaryVO(
                bookmark.getId(),
                bookmark.getTitle(),
                bookmark.getUrl(),
                creator.getDisplayName()
        );
    }

    private BookmarkDetailVO toDetail(Bookmark bookmark) {
        UserAccount creator = Optional.ofNullable(userAccountService.getById(bookmark.getCreatedBy()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator not found"));
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
        return this.lambdaQuery()
                .eq(Bookmark::getId, bookmarkId)
                .eq(Bookmark::getTeamId, teamId)
                .last("LIMIT 1")
                .oneOpt();
    }
}
