package com.bookmarkhub.bookmark.service;

import com.bookmarkhub.bookmark.dto.SaveBookmarkRequest;
import com.bookmarkhub.bookmark.entity.Bookmark;
import com.bookmarkhub.bookmark.vo.BookmarkDetailVO;
import com.bookmarkhub.bookmark.vo.BookmarkSummaryVO;
import com.bookmarkhub.shared.PageResponse;
import java.util.List;

/**
 * 书签业务接口。
 *
 * <p>不继承 IService：否则 controller 拿到 service 后可直接调用 save/removeById，
 * 绕过这里每个方法都做的 team 隔离与权限校验。跨模块需要的能力显式声明。
 */
public interface BookmarkService {

    PageResponse<BookmarkSummaryVO> list(String username);

    BookmarkDetailVO detail(String username, Long bookmarkId);

    BookmarkDetailVO create(String username, SaveBookmarkRequest request);

    BookmarkDetailVO update(String username, Long bookmarkId, SaveBookmarkRequest request);

    void delete(String username, Long bookmarkId);

    /** 供导入模块批量落库，调用方需自行保证 teamId 已正确设置。 */
    void saveImported(List<Bookmark> bookmarks);

    /** 供 dashboard 统计团队书签数。 */
    long countByTeamId(Long teamId);
}
