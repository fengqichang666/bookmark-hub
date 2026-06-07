package com.bookmarkhub.member;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bookmarkhub.auth.AuthActor;
import com.bookmarkhub.auth.AuthService;
import com.bookmarkhub.auth.TeamMember;
import com.bookmarkhub.auth.TeamMemberMapper;
import com.bookmarkhub.auth.UserAccount;
import com.bookmarkhub.auth.UserAccountMapper;
import com.bookmarkhub.shared.PageResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TeamMemberService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final TeamMemberMapper teamMemberMapper;
    private final UserAccountMapper userAccountMapper;
    private final AuthService authService;

    public TeamMemberService(
            TeamMemberMapper teamMemberMapper,
            UserAccountMapper userAccountMapper,
            AuthService authService
    ) {
        this.teamMemberMapper = teamMemberMapper;
        this.userAccountMapper = userAccountMapper;
        this.authService = authService;
    }

    @Transactional
    public TeamMemberResponse create(String username, CreateTeamMemberRequest request) {
        AuthActor actor = authService.requireActor(username);
        ensureAdmin(actor);
        if (userAccountMapper.selectCount(Wrappers.<UserAccount>lambdaQuery()
                .eq(UserAccount::getUsername, request.username())) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.username());
        user.setPasswordHash(authService.encodePassword(request.password()));
        user.setDisplayName(request.displayName());
        user.setEmail(request.username() + "@bookmarkhub.local");
        user.setStatus(ACTIVE_STATUS);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userAccountMapper.insert(user);

        TeamMember member = new TeamMember();
        member.setTeamId(actor.teamId());
        member.setUserId(user.getId());
        member.setRole(request.role());
        member.setJoinedAt(LocalDateTime.now());
        teamMemberMapper.insert(member);
        return toResponse(member, user);
    }

    public PageResponse<TeamMemberResponse> list(String username) {
        AuthActor actor = authService.requireActor(username);
        List<TeamMemberResponse> items = teamMemberMapper.selectList(Wrappers.<TeamMember>lambdaQuery()
                        .eq(TeamMember::getTeamId, actor.teamId())
                        .orderByAsc(TeamMember::getId))
                .stream()
                .map(member -> {
                    UserAccount user = Optional.ofNullable(userAccountMapper.selectById(member.getUserId()))
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
                    return toResponse(member, user);
                })
                .toList();
        return new PageResponse<>(items);
    }

    private void ensureAdmin(AuthActor actor) {
        if (!actor.isAdmin()) {
            throw new AccessDeniedException("Only admin can manage members");
        }
    }

    private TeamMemberResponse toResponse(TeamMember member, UserAccount user) {
        return new TeamMemberResponse(member.getId(), user.getUsername(), user.getDisplayName(), member.getRole());
    }
}

record CreateTeamMemberRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String displayName,
        @NotBlank String role
) {
}

record TeamMemberResponse(Long id, String username, String displayName, String role) {
}
