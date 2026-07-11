package com.bookmarkhub.bookmark.controller;

import com.bookmarkhub.bookmark.dto.SaveBookmarkRequest;
import com.bookmarkhub.bookmark.service.BookmarkService;
import com.bookmarkhub.bookmark.vo.BookmarkDetailVO;
import com.bookmarkhub.bookmark.vo.BookmarkSummaryVO;
import com.bookmarkhub.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
@Tag(name = "Bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    @Operation(summary = "List bookmarks")
    public PageResponse<BookmarkSummaryVO> list(Authentication authentication) {
        return bookmarkService.list(authentication.getName());
    }

    @GetMapping("/{bookmarkId}")
    @Operation(summary = "Get bookmark detail")
    public BookmarkDetailVO detail(@PathVariable Long bookmarkId, Authentication authentication) {
        return bookmarkService.detail(authentication.getName(), bookmarkId);
    }

    @PostMapping
    @Operation(summary = "Create bookmark")
    public BookmarkDetailVO create(@Valid @RequestBody SaveBookmarkRequest request, Authentication authentication) {
        return bookmarkService.create(authentication.getName(), request);
    }

    @PutMapping("/{bookmarkId}")
    @Operation(summary = "Update bookmark")
    public BookmarkDetailVO update(
            @PathVariable Long bookmarkId,
            @Valid @RequestBody SaveBookmarkRequest request,
            Authentication authentication
    ) {
        return bookmarkService.update(authentication.getName(), bookmarkId, request);
    }

    @DeleteMapping("/{bookmarkId}")
    @Operation(summary = "delete bookmark")
    public void delete(
            @PathVariable Long bookmarkId,
            Authentication authentication
    ) {
        bookmarkService.delete(authentication.getName(), bookmarkId);
    }
}
