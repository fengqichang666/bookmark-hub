package com.bookmarkhub.bookmark;

import com.bookmarkhub.auth.AuthActor;
import com.bookmarkhub.auth.AuthService;
import com.bookmarkhub.auth.UserAccount;
import com.bookmarkhub.auth.UserAccountRepository;
import com.bookmarkhub.category.Category;
import com.bookmarkhub.category.CategoryRepository;
import com.bookmarkhub.shared.PageResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final CategoryRepository categoryRepository;
    private final UserAccountRepository userAccountRepository;
    private final AuthService authService;

    public BookmarkService(
            BookmarkRepository bookmarkRepository,
            CategoryRepository categoryRepository,
            UserAccountRepository userAccountRepository,
            AuthService authService
    ) {
        this.bookmarkRepository = bookmarkRepository;
        this.categoryRepository = categoryRepository;
        this.userAccountRepository = userAccountRepository;
        this.authService = authService;
    }

    public PageResponse<BookmarkSummaryResponse> list(String username) {
        AuthActor actor = authService.requireActor(username);
        List<BookmarkSummaryResponse> items = bookmarkRepository.findByTeamIdOrderByIdAsc(actor.teamId())
                .stream()
                .map(this::toSummary)
                .toList();
        return new PageResponse<>(items);
    }

    public BookmarkDetailResponse create(String username, SaveBookmarkRequest request) {
        AuthActor actor = authService.requireActor(username);
        Category category = requireCategory(actor.teamId(), request.categoryId());

        Bookmark bookmark = new Bookmark();
        bookmark.setTeamId(actor.teamId());
        bookmark.setCategoryId(category.getId());
        bookmark.setTitle(request.title());
        bookmark.setUrl(request.url());
        bookmark.setDescription(request.description());
        bookmark.setCreatedBy(actor.userId());
        bookmark.setCreatedAt(LocalDateTime.now());
        bookmark.setUpdatedAt(LocalDateTime.now());
        return toDetail(bookmarkRepository.save(bookmark));
    }

    public BookmarkDetailResponse update(String username, Long bookmarkId, SaveBookmarkRequest request) {
        AuthActor actor = authService.requireActor(username);
        Bookmark bookmark = bookmarkRepository.findByIdAndTeamId(bookmarkId, actor.teamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bookmark not found"));
        if (!actor.isAdmin() && !bookmark.getCreatedBy().equals(actor.userId())) {
            throw new AccessDeniedException("Only owner or admin can modify bookmark");
        }

        Category category = requireCategory(actor.teamId(), request.categoryId());
        bookmark.setCategoryId(category.getId());
        bookmark.setTitle(request.title());
        bookmark.setUrl(request.url());
        bookmark.setDescription(request.description());
        bookmark.setUpdatedAt(LocalDateTime.now());
        return toDetail(bookmarkRepository.save(bookmark));
    }

    private Category requireCategory(Long teamId, Long categoryId) {
        return categoryRepository.findByIdAndTeamId(categoryId, teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private BookmarkSummaryResponse toSummary(Bookmark bookmark) {
        UserAccount creator = userAccountRepository.findById(bookmark.getCreatedBy())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator not found"));
        return new BookmarkSummaryResponse(
                bookmark.getId(),
                bookmark.getTitle(),
                bookmark.getUrl(),
                creator.getDisplayName()
        );
    }

    private BookmarkDetailResponse toDetail(Bookmark bookmark) {
        UserAccount creator = userAccountRepository.findById(bookmark.getCreatedBy())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator not found"));
        return new BookmarkDetailResponse(
                bookmark.getId(),
                bookmark.getTitle(),
                bookmark.getUrl(),
                bookmark.getDescription(),
                bookmark.getCategoryId(),
                creator.getDisplayName()
        );
    }
}

record SaveBookmarkRequest(
        @NotBlank String title,
        @NotBlank String url,
        String description,
        @NotNull Long categoryId
) {
}

record BookmarkSummaryResponse(Long id, String title, String url, String creatorName) {
}

record BookmarkDetailResponse(
        Long id,
        String title,
        String url,
        String description,
        Long categoryId,
        String creatorName
) {
}
