package org.study.inhamatch.domain.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.study.inhamatch.domain.profile.dto.ProfileResponse;
import org.study.inhamatch.domain.profile.dto.ProfileUpdateRequest;
import org.study.inhamatch.domain.profile.entity.DrinkingStatus;
import org.study.inhamatch.domain.profile.entity.LoveStyle;
import org.study.inhamatch.domain.profile.entity.SmokingStatus;
import org.study.inhamatch.domain.profile.service.ProfileService;
import org.study.inhamatch.global.exception.GlobalExceptionHandler;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link ProfileController}의 standalone MockMvc 슬라이스 테스트.
 * ProfileService는 Mockito로 목 처리하여 DB/Spring 컨텍스트 없이 검증(Bean Validation) 동작만 확인한다.
 */
class ProfileControllerTest {

    private static final String EMAIL = "test@inha.ac.kr";

    private MockMvc mockMvc;
    private ProfileService profileService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        profileService = Mockito.mock(ProfileService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProfileController(profileService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    void bio가_500자를_초과하면_400을_반환한다() throws Exception {
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "가".repeat(501), null, null, null, null, null);

        mockMvc.perform(updateProfileRequest(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mbti_형식이_올바르지_않으면_400을_반환한다() throws Exception {
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                null, "ABCD", null, null, null, null);

        mockMvc.perform(updateProfileRequest(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tags가_10개를_초과하면_400을_반환한다() throws Exception {
        List<String> tooManyTags = List.of(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                null, null, null, null, null, tooManyTags);

        mockMvc.perform(updateProfileRequest(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 정상_요청이면_200과_수정된_프로필을_반환한다() throws Exception {
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "안녕하세요", "INTJ", LoveStyle.CALM, SmokingStatus.NON_SMOKER,
                DrinkingStatus.SOMETIMES, List.of("영화", "독서"));

        ProfileResponse response = new ProfileResponse(
                1L, "안녕하세요", "INTJ", LoveStyle.CALM, SmokingStatus.NON_SMOKER,
                DrinkingStatus.SOMETIMES, List.of("영화", "독서"), null);
        Mockito.when(profileService.updateProfile(Mockito.eq(EMAIL), Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(updateProfileRequest(request))
                .andExpect(status().isOk());
    }

    private MockHttpServletRequestBuilder updateProfileRequest(ProfileUpdateRequest request) throws Exception {
        return put("/api/v1/profiles/me")
                .with(authentication(new UsernamePasswordAuthenticationToken(
                        EMAIL, null, List.of(new SimpleGrantedAuthority("ROLE_USER")))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }
}
