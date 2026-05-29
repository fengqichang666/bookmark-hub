package com.bookmarkhub.bookmark;

import com.bookmarkhub.shared.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public PageResponse<BookmarkSummaryResponse> list(Authentication authentication) {
        return bookmarkService.list(authentication.getName());
    }

    @GetMapping("/{bookmarkId}")
    public BookmarkDetailResponse detail(@PathVariable Long bookmarkId, Authentication authentication) {
        return bookmarkService.detail(authentication.getName(), bookmarkId);
    }

    @PostMapping
    public BookmarkDetailResponse create(@Valid @RequestBody SaveBookmarkRequest request, Authentication authentication) {
        return bookmarkService.create(authentication.getName(), request);
    }

    @PutMapping("/{bookmarkId}")
    public BookmarkDetailResponse update(
            @PathVariable Long bookmarkId,
            @Valid @RequestBody SaveBookmarkRequest request,
            Authentication authentication
    ) {
        return bookmarkService.update(authentication.getName(), bookmarkId, request);
    }
}
