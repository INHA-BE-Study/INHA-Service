package org.study.inhamatch.domain.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
