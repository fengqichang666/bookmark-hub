package com.bookmarkhub.auth;

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
    private static final String ADMIN_ROLE = "ADMIN";

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserAccountRepository userAccountRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final JwtTokenService jwtTokenService;

    public AuthService(
            UserAccountRepository userAccountRepository,
            TeamMemberRepository teamMemberRepository,
            JwtTokenService jwtTokenService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.teamMemberRepository = teamMemberRepository;
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

        Optional<UserAccount> user = userAccountRepository.findByUsernameAndStatus(username.get(), ACTIVE_STATUS);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        Optional<TeamMember> membership = teamMemberRepository.findFirstByUserIdOrderByIdAsc(user.get().getId());
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
        TeamMember membership = teamMemberRepository.findFirstByUserIdOrderByIdAsc(user.getId())
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
        return userAccountRepository.findByUsernameAndStatus(username, ACTIVE_STATUS)
                .orElseThrow(this::invalidCredentials);
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
}
