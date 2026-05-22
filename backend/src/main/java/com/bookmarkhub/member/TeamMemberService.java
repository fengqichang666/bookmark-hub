package com.bookmarkhub.member;

import com.bookmarkhub.auth.AuthActor;
import com.bookmarkhub.auth.AuthService;
import com.bookmarkhub.auth.TeamMember;
import com.bookmarkhub.auth.TeamMemberRepository;
import com.bookmarkhub.auth.UserAccount;
import com.bookmarkhub.auth.UserAccountRepository;
import com.bookmarkhub.shared.PageResponse;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TeamMemberService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final TeamMemberRepository teamMemberRepository;
    private final UserAccountRepository userAccountRepository;
    private final AuthService authService;

    public TeamMemberService(
            TeamMemberRepository teamMemberRepository,
            UserAccountRepository userAccountRepository,
            AuthService authService
    ) {
        this.teamMemberRepository = teamMemberRepository;
        this.userAccountRepository = userAccountRepository;
        this.authService = authService;
    }

    public TeamMemberResponse create(String username, CreateTeamMemberRequest request) {
        AuthActor actor = authService.requireActor(username);
        ensureAdmin(actor);
        if (userAccountRepository.existsByUsername(request.username())) {
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
        UserAccount savedUser = userAccountRepository.save(user);

        TeamMember member = new TeamMember();
        member.setTeamId(actor.teamId());
        member.setUserId(savedUser.getId());
        member.setRole(request.role());
        member.setJoinedAt(LocalDateTime.now());
        TeamMember savedMember = teamMemberRepository.save(member);
        return toResponse(savedMember, savedUser);
    }

    public PageResponse<TeamMemberResponse> list(String username) {
        AuthActor actor = authService.requireActor(username);
        List<TeamMemberResponse> items = teamMemberRepository.findByTeamIdOrderByIdAsc(actor.teamId())
                .stream()
                .map(member -> {
                    UserAccount user = userAccountRepository.findById(member.getUserId())
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
