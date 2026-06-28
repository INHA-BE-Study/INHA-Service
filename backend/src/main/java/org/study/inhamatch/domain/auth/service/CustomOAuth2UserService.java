package org.study.inhamatch.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.study.inhamatch.domain.auth.entity.User;
import org.study.inhamatch.domain.auth.repository.UserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        if (email == null || !email.endsWith("@inha.edu")) {
            throw new OAuth2AuthenticationException("인하대학교 이메일(@inha.edu)만 가입 가능합니다.");
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.isDeleted()) {
                throw new OAuth2AuthenticationException("탈퇴한 계정입니다.");
            }
        });

        userRepository.findByEmail(email)
                .orElseGet(() -> {
                    String tempStudentId = email.split("@")[0];
                    return userRepository.save(User.create(email, tempStudentId));
                });

        return oAuth2User;
    }
}
