package com.bookmarkhub.auth.service;

import com.bookmarkhub.auth.dto.LoginRequest;
import com.bookmarkhub.auth.vo.CurrentUserVO;
import com.bookmarkhub.auth.vo.LoginVO;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface AuthService {

    LoginVO login(LoginRequest request);

    CurrentUserVO currentUser(String username);

    Optional<UsernamePasswordAuthenticationToken> resolveAuthentication(String token);

    AuthActor requireActor(String username);

    String encodePassword(String rawPassword);
}
