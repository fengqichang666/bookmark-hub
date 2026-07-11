package com.bookmarkhub.bookmark.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmarkhub.bookmark.dto.SaveBookmarkRequest;
import com.bookmarkhub.bookmark.entity.Bookmark;
import com.bookmarkhub.bookmark.vo.BookmarkDetailVO;
import com.bookmarkhub.bookmark.vo.BookmarkSummaryVO;
import com.bookmarkhub.shared.PageResponse;

public interface BookmarkService extends IService<Bookmark> {

    PageResponse<BookmarkSummaryVO> list(String username);

    BookmarkDetailVO detail(String username, Long bookmarkId);

    BookmarkDetailVO create(String username, SaveBookmarkRequest request);

    BookmarkDetailVO update(String username, Long bookmarkId, SaveBookmarkRequest request);

    void delete(String username, Long bookmarkId);
}
