package org.study.inhamatch.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.inhamatch.domain.auth.entity.RefreshToken;
import org.study.inhamatch.domain.auth.entity.User;
import org.study.inhamatch.domain.auth.repository.RefreshTokenRepository;
import org.study.inhamatch.global.exception.BusinessException;
import org.study.inhamatch.global.exception.ErrorCode;
import org.study.inhamatch.global.jwt.JwtProperties;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshToken create(User user) {
        RefreshToken refreshToken = RefreshToken.create(user, jwtProperties.getRefreshTokenValidityMs());
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validate(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        if (refreshToken.getUser().isDeleted()) {
            throw new BusinessException(ErrorCode.DELETED_USER);
        }

        return refreshToken;
    }

    public void delete(String tokenValue) {
        refreshTokenRepository.deleteByToken(tokenValue);
    }

    public void deleteAllByUser(User user) {
        refreshTokenRepository.deleteAllByUser(user);
    }
}
