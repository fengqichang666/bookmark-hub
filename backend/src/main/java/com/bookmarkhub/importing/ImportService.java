package com.bookmarkhub.importing;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bookmarkhub.auth.AuthActor;
import com.bookmarkhub.auth.AuthService;
import com.bookmarkhub.bookmark.Bookmark;
import com.bookmarkhub.bookmark.BookmarkMapper;
import com.bookmarkhub.category.Category;
import com.bookmarkhub.category.CategoryMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImportService {

    private static final String IMPORT_SUCCESS = "SUCCESS";

    private final AuthService authService;
    private final CategoryMapper categoryMapper;
    private final BookmarkMapper bookmarkMapper;
    private final ImportRecordMapper importRecordMapper;

    public ImportService(
            AuthService authService,
            CategoryMapper categoryMapper,
            BookmarkMapper bookmarkMapper,
            ImportRecordMapper importRecordMapper
    ) {
        this.authService = authService;
        this.categoryMapper = categoryMapper;
        this.bookmarkMapper = bookmarkMapper;
        this.importRecordMapper = importRecordMapper;
    }

    public ImportPreviewResponse parse(String username, MultipartFile file) {
        authService.requireActor(username);
        return new ImportPreviewResponse(file.getOriginalFilename(), parseItems(file));
    }

    @Transactional
    public ImportResultResponse confirm(String username, @Valid ConfirmImportRequest request) {
        AuthActor actor = authService.requireActor(username);
        Category category = Optional.ofNullable(categoryMapper.selectOne(Wrappers.<Category>lambdaQuery()
                        .eq(Category::getId, request.categoryId())
                        .eq(Category::getTeamId, actor.teamId())
                        .last("LIMIT 1")))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        for (ImportPreviewItem item : request.items()) {
            Bookmark bookmark = new Bookmark();
            bookmark.setTeamId(actor.teamId());
            bookmark.setCategoryId(category.getId());
            bookmark.setTitle(item.title());
            bookmark.setUrl(item.url());
            bookmark.setDescription(item.folderPath());
            bookmark.setCreatedBy(actor.userId());
            bookmark.setCreatedAt(LocalDateTime.now());
            bookmark.setUpdatedAt(LocalDateTime.now());
            bookmarkMapper.insert(bookmark);
        }

        ImportRecord record = new ImportRecord();
        record.setTeamId(actor.teamId());
        record.setOperatorUserId(actor.userId());
        record.setFileName(request.fileName());
        record.setTotalCount(request.items().size());
        record.setSuccessCount(request.items().size());
        record.setFailedCount(0);
        record.setStatus(IMPORT_SUCCESS);
        record.setCreatedAt(LocalDateTime.now());
        importRecordMapper.insert(record);

        return new ImportResultResponse(request.items().size(), request.items().size(), 0);
    }

    private List<ImportPreviewItem> parseItems(MultipartFile file) {
        try {
            Document document = Jsoup.parse(file.getInputStream(), StandardCharsets.UTF_8.name(), "");
            List<ImportPreviewItem> items = new ArrayList<>();
            collectBookmarks(document, new ArrayList<>(), items);
            return items;
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to read import file", exception);
        }
    }

    // Netscape bookmark exports model folders as H3 + sibling DL blocks, so we carry the path during recursion.
    private void collectBookmarks(Element parent, List<String> folderParts, List<ImportPreviewItem> items) {
        String pendingFolder = null;
        for (Element child : parent.children()) {
            if ("dt".equals(child.tagName())) {
                for (Element directChild : child.children()) {
                    if ("h3".equals(directChild.tagName())) {
                        pendingFolder = directChild.text();
                    } else if ("a".equals(directChild.tagName()) && directChild.hasAttr("href")) {
                        items.add(new ImportPreviewItem(
                                directChild.text(),
                                directChild.attr("href"),
                                String.join("/", folderParts)
                        ));
                    } else if ("dl".equals(directChild.tagName())) {
                        List<String> nextFolderParts = new ArrayList<>(folderParts);
                        if (pendingFolder != null && !pendingFolder.isBlank()) {
                            nextFolderParts.add(pendingFolder);
                        }
                        collectBookmarks(directChild, nextFolderParts, items);
                        pendingFolder = null;
                    }
                }
                continue;
            }

            if ("dl".equals(child.tagName())) {
                List<String> nextFolderParts = new ArrayList<>(folderParts);
                if (pendingFolder != null && !pendingFolder.isBlank()) {
                    nextFolderParts.add(pendingFolder);
                }
                collectBookmarks(child, nextFolderParts, items);
                pendingFolder = null;
                continue;
            }

            collectBookmarks(child, folderParts, items);
        }
    }
}

record ImportPreviewResponse(String fileName, List<ImportPreviewItem> items) {
}

record ImportResultResponse(int totalCount, int successCount, int failedCount) {
}

record ConfirmImportRequest(
        @NotBlank String fileName,
        @NotNull Long categoryId,
        @NotEmpty List<@Valid ImportPreviewItem> items
) {
}

record ImportPreviewItem(
        @NotBlank String title,
        @NotBlank String url,
        String folderPath
) {
}
