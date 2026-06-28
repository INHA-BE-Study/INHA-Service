package org.study.inhamatch.global.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final OAuth2Properties oauth2Properties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = "로그인에 실패했습니다.";
        }

        String redirectUrl = UriComponentsBuilder
                .fromUriString(oauth2Properties.getRedirectUri())
                .queryParam("error", message)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
