package org.study.inhamatch.domain.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.inhamatch.domain.auth.entity.User;
import org.study.inhamatch.domain.auth.repository.UserRepository;
import org.study.inhamatch.domain.profile.dto.ProfileResponse;
import org.study.inhamatch.domain.profile.dto.ProfileUpdateRequest;
import org.study.inhamatch.domain.profile.entity.Profile;
import org.study.inhamatch.domain.profile.entity.ProfileTag;
import org.study.inhamatch.domain.profile.repository.ProfileRepository;
import org.study.inhamatch.domain.profile.repository.ProfileTagRepository;
import org.study.inhamatch.global.exception.BusinessException;
import org.study.inhamatch.global.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileTagRepository profileTagRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(String email) {
        User user = getActiveUser(email);
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        List<ProfileTag> tags = profileTagRepository.findAllByProfile(profile);
        return ProfileResponse.from(profile, tags);
    }

    public ProfileResponse updateProfile(String email, ProfileUpdateRequest request) {
        User user = getActiveUser(email);
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseGet(() -> profileRepository.save(Profile.create(user.getId())));

        profile.update(request.bio(), request.mbti(), request.loveStyle(),
                request.smokingStatus(), request.drinkingStatus());

        if (request.tags() != null) {
            profileTagRepository.deleteAllByProfile(profile);
            List<ProfileTag> newTags = request.tags().stream()
                    .map(tag -> ProfileTag.create(profile, tag))
                    .toList();
            profileTagRepository.saveAll(newTags);
        }

        List<ProfileTag> tags = profileTagRepository.findAllByProfile(profile);
        return ProfileResponse.from(profile, tags);
    }

    private User getActiveUser(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
