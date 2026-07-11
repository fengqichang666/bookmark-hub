package com.bookmarkhub.auth.controller;

import com.bookmarkhub.auth.dto.LoginRequest;
import com.bookmarkhub.auth.service.AuthService;
import com.bookmarkhub.auth.vo.CurrentUserVO;
import com.bookmarkhub.auth.vo.LoginVO;
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
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    public LoginVO login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user")
    public CurrentUserVO me(Authentication authentication) {
        return authService.currentUser(authentication.getName());
    }
}
