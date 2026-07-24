package org.study.inhamatch.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.study.inhamatch.domain.profile.entity.DrinkingStatus;
import org.study.inhamatch.domain.profile.entity.LoveStyle;
import org.study.inhamatch.domain.profile.entity.SmokingStatus;

import java.util.List;

public record ProfileUpdateRequest(
        @Size(max = 500, message = "자기소개는 500자를 초과할 수 없습니다.")
        String bio,

        @Pattern(regexp = "^[EI][SN][TF][JP]$", message = "MBTI 형식이 올바르지 않습니다.")
        String mbti,

        LoveStyle loveStyle,
        SmokingStatus smokingStatus,
        DrinkingStatus drinkingStatus,

        @Size(max = 10, message = "관심사는 최대 10개까지 등록할 수 있습니다.")
        List<@NotBlank String> tags
) {
}
