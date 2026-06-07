package com.bookmarkhub.dashboard;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bookmarkhub.auth.AuthActor;
import com.bookmarkhub.auth.AuthService;
import com.bookmarkhub.auth.TeamMember;
import com.bookmarkhub.auth.TeamMemberMapper;
import com.bookmarkhub.bookmark.Bookmark;
import com.bookmarkhub.bookmark.BookmarkMapper;
import com.bookmarkhub.category.Category;
import com.bookmarkhub.category.CategoryMapper;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final AuthService authService;
    private final BookmarkMapper bookmarkMapper;
    private final CategoryMapper categoryMapper;
    private final TeamMemberMapper teamMemberMapper;

    public DashboardService(
            AuthService authService,
            BookmarkMapper bookmarkMapper,
            CategoryMapper categoryMapper,
            TeamMemberMapper teamMemberMapper
    ) {
        this.authService = authService;
        this.bookmarkMapper = bookmarkMapper;
        this.categoryMapper = categoryMapper;
        this.teamMemberMapper = teamMemberMapper;
    }

    public DashboardOverviewResponse overview(String username) {
        AuthActor actor = authService.requireActor(username);
        return new DashboardOverviewResponse(
                bookmarkMapper.selectCount(Wrappers.<Bookmark>lambdaQuery().eq(Bookmark::getTeamId, actor.teamId())),
                categoryMapper.selectCount(Wrappers.<Category>lambdaQuery().eq(Category::getTeamId, actor.teamId())),
                teamMemberMapper.selectCount(Wrappers.<TeamMember>lambdaQuery().eq(TeamMember::getTeamId, actor.teamId()))
        );
    }
}

record DashboardOverviewResponse(long bookmarkCount, long categoryCount, long memberCount) {
}
