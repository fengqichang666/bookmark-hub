package com.bookmarkhub.member.service.impl;

import com.bookmarkhub.auth.entity.TeamMember;
import com.bookmarkhub.auth.entity.UserAccount;
import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.auth.service.AuthService;
import com.bookmarkhub.auth.service.TeamMemberService;
import com.bookmarkhub.auth.service.UserAccountService;
import com.bookmarkhub.member.dto.CreateTeamMemberRequest;
import com.bookmarkhub.member.service.MemberService;
import com.bookmarkhub.member.vo.TeamMemberVO;
import com.bookmarkhub.shared.PageResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final TeamMemberService teamMemberService;
    private final UserAccountService userAccountService;
    private final AuthService authService;

    @Override
    @Transactional
    public TeamMemberVO create(String username, CreateTeamMemberRequest request) {
        AuthActor actor = authService.requireActor(username);
        ensureAdmin(actor);
        long exists = userAccountService.lambdaQuery()
                .eq(UserAccount::getUsername, request.getUsername())
                .count();
        if (exists > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.getUsername());
        user.setPasswordHash(authService.encodePassword(request.getPassword()));
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getUsername() + "@bookmarkhub.local");
        user.setStatus(ACTIVE_STATUS);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userAccountService.save(user);

        TeamMember member = new TeamMember();
        member.setTeamId(actor.teamId());
        member.setUserId(user.getId());
        member.setRole(request.getRole());
        member.setJoinedAt(LocalDateTime.now());
        teamMemberService.save(member);
        return toVO(member, user);
    }

    @Override
    public PageResponse<TeamMemberVO> list(String username) {
        AuthActor actor = authService.requireActor(username);
        List<TeamMemberVO> items = teamMemberService.lambdaQuery()
                .eq(TeamMember::getTeamId, actor.teamId())
                .orderByAsc(TeamMember::getId)
                .list()
                .stream()
                .map(member -> {
                    UserAccount user = Optional.ofNullable(userAccountService.getById(member.getUserId()))
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
                    return toVO(member, user);
                })
                .toList();
        return new PageResponse<>(items);
    }

    private void ensureAdmin(AuthActor actor) {
        if (!actor.isAdmin()) {
            throw new AccessDeniedException("Only admin can manage members");
        }
    }

    private TeamMemberVO toVO(TeamMember member, UserAccount user) {
        return new TeamMemberVO(member.getId(), user.getUsername(), user.getDisplayName(), member.getRole());
    }
}
