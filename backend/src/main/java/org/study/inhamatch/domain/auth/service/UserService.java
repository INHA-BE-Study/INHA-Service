package org.study.inhamatch.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.inhamatch.domain.auth.dto.SignupRequest;
import org.study.inhamatch.domain.auth.dto.UserResponse;
import org.study.inhamatch.domain.auth.entity.User;
import org.study.inhamatch.domain.auth.repository.UserRepository;
import org.study.inhamatch.global.exception.BusinessException;
import org.study.inhamatch.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public UserResponse signup(String email, SignupRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        try {
            user.completeSignup(request.grade(), request.gender());
        } catch (IllegalStateException e) {
            throw new BusinessException(ErrorCode.ALREADY_COMPLETED_SIGNUP);
        }

        return UserResponse.from(user);
    }

    public void deleteUser(Long userId, String email) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        user.softDelete();
        refreshTokenService.deleteAllByUser(user);
    }
}
