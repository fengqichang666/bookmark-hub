package com.bookmarkhub.importing.service.impl;

import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.auth.service.AuthService;
import com.bookmarkhub.bookmark.entity.Bookmark;
import com.bookmarkhub.bookmark.service.BookmarkService;
import com.bookmarkhub.category.entity.Category;
import com.bookmarkhub.category.service.CategoryService;
import com.bookmarkhub.importing.dto.ConfirmImportRequest;
import com.bookmarkhub.importing.dto.ImportPreviewItem;
import com.bookmarkhub.importing.entity.ImportRecord;
import com.bookmarkhub.importing.service.ImportRecordService;
import com.bookmarkhub.importing.service.ImportService;
import com.bookmarkhub.importing.vo.ImportPreviewVO;
import com.bookmarkhub.importing.vo.ImportResultVO;
import com.bookmarkhub.shared.BizException;
import com.bookmarkhub.shared.ErrorCode;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private static final String IMPORT_SUCCESS = "SUCCESS";

    private final AuthService authService;
    private final CategoryService categoryService;
    private final BookmarkService bookmarkService;
    private final ImportRecordService importRecordService;

    @Override
    public ImportPreviewVO parse(String username, MultipartFile file) {
        authService.requireActor(username);
        return new ImportPreviewVO(file.getOriginalFilename(), parseItems(file));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultVO confirm(String username, ConfirmImportRequest request) {
        AuthActor actor = authService.requireActor(username);
        Category category = categoryService.findByIdAndTeamId(request.getCategoryId(), actor.teamId())
                .orElseThrow(() -> new BizException(ErrorCode.CATEGORY_NOT_FOUND));

        List<Bookmark> bookmarks = new ArrayList<>();
        for (ImportPreviewItem item : request.getItems()) {
            Bookmark bookmark = new Bookmark();
            bookmark.setTeamId(actor.teamId());
            bookmark.setCategoryId(category.getId());
            bookmark.setTitle(item.getTitle());
            bookmark.setUrl(item.getUrl());
            bookmark.setDescription(item.getFolderPath());
            bookmark.setCreatedBy(actor.userId());
            bookmarks.add(bookmark);
        }
        bookmarkService.saveImported(bookmarks);

        ImportRecord record = new ImportRecord();
        record.setTeamId(actor.teamId());
        record.setOperatorUserId(actor.userId());
        record.setFileName(request.getFileName());
        record.setTotalCount(request.getItems().size());
        record.setSuccessCount(request.getItems().size());
        record.setFailedCount(0);
        record.setStatus(IMPORT_SUCCESS);
        importRecordService.save(record);

        return new ImportResultVO(request.getItems().size(), request.getItems().size(), 0);
    }

    private List<ImportPreviewItem> parseItems(MultipartFile file) {
        try {
            Document document = Jsoup.parse(file.getInputStream(), StandardCharsets.UTF_8.name(), "");
            List<ImportPreviewItem> items = new ArrayList<>();
            collectBookmarks(document, new ArrayList<>(), items);
            return items;
        } catch (IOException exception) {
            throw new BizException(ErrorCode.IMPORT_FILE_UNREADABLE, exception);
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
