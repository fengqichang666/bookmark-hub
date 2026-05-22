package com.bookmarkhub.dashboard;

import com.bookmarkhub.auth.AuthActor;
import com.bookmarkhub.auth.AuthService;
import com.bookmarkhub.auth.TeamMemberRepository;
import com.bookmarkhub.bookmark.BookmarkRepository;
import com.bookmarkhub.category.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final AuthService authService;
    private final BookmarkRepository bookmarkRepository;
    private final CategoryRepository categoryRepository;
    private final TeamMemberRepository teamMemberRepository;

    public DashboardService(
            AuthService authService,
            BookmarkRepository bookmarkRepository,
            CategoryRepository categoryRepository,
            TeamMemberRepository teamMemberRepository
    ) {
        this.authService = authService;
        this.bookmarkRepository = bookmarkRepository;
        this.categoryRepository = categoryRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public DashboardOverviewResponse overview(String username) {
        AuthActor actor = authService.requireActor(username);
        return new DashboardOverviewResponse(
                bookmarkRepository.countByTeamId(actor.teamId()),
                categoryRepository.countByTeamId(actor.teamId()),
                teamMemberRepository.countByTeamId(actor.teamId())
        );
    }
}

record DashboardOverviewResponse(long bookmarkCount, long categoryCount, long memberCount) {
}
