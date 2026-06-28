package org.study.inhamatch.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.study.inhamatch.domain.auth.dto.ReissueRequest;
import org.study.inhamatch.domain.auth.dto.SignupRequest;
import org.study.inhamatch.domain.auth.dto.TokenResponse;
import org.study.inhamatch.domain.auth.dto.UserResponse;
import org.study.inhamatch.domain.auth.service.AuthService;
import org.study.inhamatch.domain.auth.service.UserService;
import org.study.inhamatch.global.response.ApiResponse;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@AuthenticationPrincipal String email,
                                                            @Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.signup(email, request)));
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@Valid @RequestBody ReissueRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.reissue(request)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId,
                                                        @AuthenticationPrincipal String email) {
        userService.deleteUser(userId, email);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다.", null));
    }
}
