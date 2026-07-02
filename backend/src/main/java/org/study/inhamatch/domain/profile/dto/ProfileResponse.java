package org.study.inhamatch.domain.profile.dto;

import org.study.inhamatch.domain.profile.entity.DrinkingStatus;
import org.study.inhamatch.domain.profile.entity.LoveStyle;
import org.study.inhamatch.domain.profile.entity.Profile;
import org.study.inhamatch.domain.profile.entity.ProfileTag;
import org.study.inhamatch.domain.profile.entity.SmokingStatus;

import java.util.List;

public record ProfileResponse(
        Long userId,
        String bio,
        String mbti,
        LoveStyle loveStyle,
        SmokingStatus smokingStatus,
        DrinkingStatus drinkingStatus,
        List<String> tags
) {
    public static ProfileResponse from(Profile profile, List<ProfileTag> tags) {
        return new ProfileResponse(
                profile.getUserId(),
                profile.getBio(),
                profile.getMbti(),
                profile.getLoveStyle(),
                profile.getSmokingStatus(),
                profile.getDrinkingStatus(),
                tags.stream().map(ProfileTag::getTagValue).toList()
        );
    }
}
