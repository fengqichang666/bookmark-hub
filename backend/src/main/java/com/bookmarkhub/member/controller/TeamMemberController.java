package com.bookmarkhub.member.controller;

import com.bookmarkhub.member.dto.CreateTeamMemberRequest;
import com.bookmarkhub.member.service.MemberService;
import com.bookmarkhub.member.vo.TeamMemberVO;
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

    private final MemberService memberService;

    public TeamMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @Operation(summary = "List team members")
    public PageResponse<TeamMemberVO> list(Authentication authentication) {
        return memberService.list(authentication.getName());
    }

    @PostMapping
    @Operation(summary = "Create team member")
    public TeamMemberVO create(
            @Valid @RequestBody CreateTeamMemberRequest request,
            Authentication authentication
    ) {
        return memberService.create(authentication.getName(), request);
    }
}
