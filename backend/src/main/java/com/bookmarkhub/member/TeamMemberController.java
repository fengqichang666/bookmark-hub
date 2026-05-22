package com.bookmarkhub.member;

import com.bookmarkhub.shared.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/members", "/api/team-members"})
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @GetMapping
    public PageResponse<TeamMemberResponse> list(Authentication authentication) {
        return teamMemberService.list(authentication.getName());
    }

    @PostMapping
    public TeamMemberResponse create(
            @Valid @RequestBody CreateTeamMemberRequest request,
            Authentication authentication
    ) {
        return teamMemberService.create(authentication.getName(), request);
    }
}
