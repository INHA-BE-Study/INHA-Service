package org.study.inhamatch.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.inhamatch.domain.auth.dto.LogoutRequest;
import org.study.inhamatch.domain.auth.dto.ReissueRequest;
import org.study.inhamatch.domain.auth.dto.TokenResponse;
import org.study.inhamatch.domain.auth.dto.UserResponse;
import org.study.inhamatch.domain.auth.entity.RefreshToken;
import org.study.inhamatch.domain.auth.entity.User;
import org.study.inhamatch.domain.auth.repository.RefreshTokenRepository;
import org.study.inhamatch.domain.auth.repository.UserRepository;
import org.study.inhamatch.global.exception.BusinessException;
import org.study.inhamatch.global.exception.ErrorCode;
import org.study.inhamatch.global.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String email) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    public void logout(LogoutRequest request) {
        refreshTokenService.delete(request.refreshToken());
    }

    public TokenResponse reissue(ReissueRequest request) {
        RefreshToken refreshToken = refreshTokenService.validate(request.refreshToken());
        User user = refreshToken.getUser();

        refreshTokenRepository.delete(refreshToken);

        String accessToken = jwtTokenProvider.createToken(user.getEmail());
        RefreshToken newRefreshToken = refreshTokenService.create(user);

        return new TokenResponse(accessToken, newRefreshToken.getToken());
    }
}
