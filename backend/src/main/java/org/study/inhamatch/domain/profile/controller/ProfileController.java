package org.study.inhamatch.domain.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.study.inhamatch.domain.profile.dto.ProfileResponse;
import org.study.inhamatch.domain.profile.dto.ProfileUpdateRequest;
import org.study.inhamatch.domain.profile.service.ProfileService;
import org.study.inhamatch.global.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(ApiResponse.success(profileService.getMyProfile(email)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal String email,
            @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(profileService.updateProfile(email, request)));
    }
}
