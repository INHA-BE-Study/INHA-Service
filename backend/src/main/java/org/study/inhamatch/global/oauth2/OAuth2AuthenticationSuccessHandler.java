package org.study.inhamatch.global.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.study.inhamatch.domain.auth.entity.RefreshToken;
import org.study.inhamatch.domain.auth.entity.User;
import org.study.inhamatch.domain.auth.repository.UserRepository;
import org.study.inhamatch.domain.auth.service.RefreshTokenService;
import org.study.inhamatch.global.jwt.JwtTokenProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2Properties oauth2Properties;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            redirectWithError(response, "이메일 정보를 가져올 수 없습니다.");
            return;
        }

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElse(null);

        if (user == null) {
            redirectWithError(response, "사용자를 찾을 수 없습니다.");
            return;
        }

        String accessToken = jwtTokenProvider.createToken(email);
        RefreshToken refreshToken = refreshTokenService.create(user);

        String redirectUrl = UriComponentsBuilder
                .fromUriString(oauth2Properties.getRedirectUri())
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken.getToken())
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private void redirectWithError(HttpServletResponse response, String message) throws IOException {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(oauth2Properties.getRedirectUri())
                .queryParam("error", message)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
