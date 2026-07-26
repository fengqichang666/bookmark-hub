package com.bookmarkhub.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bookmarkhub.auth.dto.LoginRequest;
import com.bookmarkhub.auth.entity.TeamMember;
import com.bookmarkhub.auth.entity.UserAccount;
import com.bookmarkhub.auth.service.AuthActor;
import com.bookmarkhub.auth.service.AuthService;
import com.bookmarkhub.auth.service.JwtTokenService;
import com.bookmarkhub.auth.service.TeamMemberService;
import com.bookmarkhub.auth.service.UserAccountService;
import com.bookmarkhub.auth.vo.CurrentUserVO;
import com.bookmarkhub.auth.vo.LoginVO;
import com.bookmarkhub.shared.BizException;
import com.bookmarkhub.shared.ErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final PasswordEncoder passwordEncoder;
    private final UserAccountService userAccountService;
    private final TeamMemberService teamMemberService;
    private final JwtTokenService jwtTokenService;

    @Override
    public LoginVO login(LoginRequest request) {
        AuthActor actor = requireActor(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), actor.getUser().getPasswordHash())) {
            throw invalidCredentials();
        }
        return new LoginVO(jwtTokenService.generateToken(actor.username()), toCurrentUser(actor));
    }

    @Override
    public CurrentUserVO currentUser(String username) {
        return toCurrentUser(requireActor(username));
    }

    @Override
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

    @Override
    public AuthActor requireActor(String username) {
        UserAccount user = findActiveUser(username);
        TeamMember membership = findFirstMembership(user.getId())
                .orElseThrow(this::invalidCredentials);
        return new AuthActor(user, membership);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private CurrentUserVO toCurrentUser(AuthActor actor) {
        return new CurrentUserVO(
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
        // username 有唯一约束，但用 getOne(w, false) 表达“取第一条”比拼 last("LIMIT 1") 更安全
        return Optional.ofNullable(userAccountService.getOne(
                Wrappers.<UserAccount>lambdaQuery()
                        .eq(UserAccount::getUsername, username)
                        .eq(UserAccount::getStatus, ACTIVE_STATUS),
                false));
    }

    private Optional<TeamMember> findFirstMembership(Long userId) {
        return Optional.ofNullable(teamMemberService.getOne(
                Wrappers.<TeamMember>lambdaQuery()
                        .eq(TeamMember::getUserId, userId)
                        .orderByAsc(TeamMember::getId),
                false));
    }

    private BizException invalidCredentials() {
        return new BizException(ErrorCode.INVALID_CREDENTIALS);
    }
}
