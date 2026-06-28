package org.study.inhamatch.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.study.inhamatch.domain.auth.dto.LogoutRequest;
import org.study.inhamatch.domain.auth.dto.UserResponse;
import org.study.inhamatch.domain.auth.service.AuthService;
import org.study.inhamatch.global.response.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(ApiResponse.success(authService.getMyProfile(email)));
    }

    @GetMapping("/login-url")
    public ResponseEntity<ApiResponse<Map<String, String>>> loginUrl() {
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", "/oauth2/authorization/google")));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다.", null));
    }
}
