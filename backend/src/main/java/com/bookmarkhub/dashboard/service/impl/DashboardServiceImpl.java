package com.bookmarkhub.dashboard.service.impl;

import com.bookmarkhub.auth.entity.TeamMember;
import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.auth.service.AuthService;
import com.bookmarkhub.auth.service.TeamMemberService;
import com.bookmarkhub.bookmark.entity.Bookmark;
import com.bookmarkhub.bookmark.service.BookmarkService;
import com.bookmarkhub.category.entity.Category;
import com.bookmarkhub.category.service.CategoryService;
import com.bookmarkhub.dashboard.service.DashboardService;
import com.bookmarkhub.dashboard.vo.DashboardOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AuthService authService;
    private final BookmarkService bookmarkService;
    private final CategoryService categoryService;
    private final TeamMemberService teamMemberService;

    @Override
    public DashboardOverviewVO overview(String username) {
        AuthActor actor = authService.requireActor(username);
        long bookmarkCount = bookmarkService.lambdaQuery()
                .eq(Bookmark::getTeamId, actor.teamId())
                .count();
        long categoryCount = categoryService.lambdaQuery()
                .eq(Category::getTeamId, actor.teamId())
                .count();
        long memberCount = teamMemberService.lambdaQuery()
                .eq(TeamMember::getTeamId, actor.teamId())
                .count();
        return new DashboardOverviewVO(bookmarkCount, categoryCount, memberCount);
    }
}
