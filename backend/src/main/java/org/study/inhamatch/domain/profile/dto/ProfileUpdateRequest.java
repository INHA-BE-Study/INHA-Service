package org.study.inhamatch.domain.profile.dto;

import org.study.inhamatch.domain.profile.entity.DrinkingStatus;
import org.study.inhamatch.domain.profile.entity.LoveStyle;
import org.study.inhamatch.domain.profile.entity.SmokingStatus;

import java.util.List;

public record ProfileUpdateRequest(
        String bio,
        String mbti,
        LoveStyle loveStyle,
        SmokingStatus smokingStatus,
        DrinkingStatus drinkingStatus,
        List<String> tags
) {
}
