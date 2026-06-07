package com.bookmarkhub.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserAccountMapper userAccountMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final JwtTokenService jwtTokenService;

    public AuthService(
            UserAccountMapper userAccountMapper,
            TeamMemberMapper teamMemberMapper,
            JwtTokenService jwtTokenService
    ) {
        this.userAccountMapper = userAccountMapper;
        this.teamMemberMapper = teamMemberMapper;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        AuthActor actor = requireActor(request.username());
        if (!passwordEncoder.matches(request.password(), actor.user().getPasswordHash())) {
            throw invalidCredentials();
        }

        return new LoginResponse(jwtTokenService.generateToken(actor.username()), toCurrentUser(actor));
    }

    public CurrentUserResponse currentUser(String username) {
        return toCurrentUser(requireActor(username));
    }

    public Optional<UsernamePasswordAuthenticationToken> resolveAuthentication(String token) {
        Optional<String> username = jwtTokenService.extractUsername(token);
        if (username.isEmpty()) {
            return Optional.empty();
        }

        Optional<UserAccount> user = findActiveUserOptional(username.get());
        if (user.isEmpty()) {
            return Optional.empty();
        }

        Optional<TeamMember> membership = findFirstMembership(user.get().getId());
        if (membership.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new UsernamePasswordAuthenticationToken(
                user.get().getUsername(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + membership.get().getRole()))
        ));
    }

    public AuthActor requireActor(String username) {
        UserAccount user = findActiveUser(username);
        TeamMember membership = findFirstMembership(user.getId())
                .orElseThrow(this::invalidCredentials);
        return new AuthActor(user, membership);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private CurrentUserResponse toCurrentUser(AuthActor actor) {
        return new CurrentUserResponse(
                actor.username(),
                actor.displayName(),
                actor.role(),
                actor.teamId()
        );
    }

    private UserAccount findActiveUser(String username) {
        return findActiveUserOptional(username)
                .orElseThrow(this::invalidCredentials);
    }

    private Optional<UserAccount> findActiveUserOptional(String username) {
        LambdaQueryWrapper<UserAccount> query = Wrappers.<UserAccount>lambdaQuery()
                .eq(UserAccount::getUsername, username)
                .eq(UserAccount::getStatus, ACTIVE_STATUS)
                .last("LIMIT 1");
        return Optional.ofNullable(userAccountMapper.selectOne(query));
    }

    private Optional<TeamMember> findFirstMembership(Long userId) {
        LambdaQueryWrapper<TeamMember> query = Wrappers.<TeamMember>lambdaQuery()
                .eq(TeamMember::getUserId, userId)
                .orderByAsc(TeamMember::getId)
                .last("LIMIT 1");
        return Optional.ofNullable(teamMemberMapper.selectOne(query));
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
}
