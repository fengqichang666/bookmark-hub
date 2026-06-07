package com.bookmarkhub.member;

import com.bookmarkhub.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/members", "/api/team-members"})
@Tag(name = "Members")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @GetMapping
    @Operation(summary = "List team members")
    public PageResponse<TeamMemberResponse> list(Authentication authentication) {
        return teamMemberService.list(authentication.getName());
    }

    @PostMapping
    @Operation(summary = "Create team member")
    public TeamMemberResponse create(
            @Valid @RequestBody CreateTeamMemberRequest request,
            Authentication authentication
    ) {
        return teamMemberService.create(authentication.getName(), request);
    }
}
