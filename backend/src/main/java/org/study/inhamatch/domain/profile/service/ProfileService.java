package org.study.inhamatch.domain.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
import org.study.inhamatch.global.s3.S3Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileTagRepository profileTagRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(String email) {
        User user = getActiveUser(email);
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        List<ProfileTag> tags = profileTagRepository.findAllByProfile(profile);
        String photoUrl = profile.hasPhoto() ? s3Service.generatePresignedUrl(profile.getPhotoKey()) : null;
        return ProfileResponse.from(profile, tags, photoUrl);
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
        String photoUrl = profile.hasPhoto() ? s3Service.generatePresignedUrl(profile.getPhotoKey()) : null;
        return ProfileResponse.from(profile, tags, photoUrl);
    }

    public ProfileResponse uploadPhoto(String email, MultipartFile file) {
        User user = getActiveUser(email);
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseGet(() -> profileRepository.save(Profile.create(user.getId())));

        if (profile.hasPhoto()) {
            s3Service.delete(profile.getPhotoKey());
        }

        String newKey = s3Service.upload(file, user.getId());
        profile.updatePhotoKey(newKey);

        List<ProfileTag> tags = profileTagRepository.findAllByProfile(profile);
        return ProfileResponse.from(profile, tags, s3Service.generatePresignedUrl(newKey));
    }

    public void deletePhoto(String email) {
        User user = getActiveUser(email);
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        if (!profile.hasPhoto()) {
            throw new BusinessException(ErrorCode.PROFILE_PHOTO_NOT_FOUND);
        }

        s3Service.delete(profile.getPhotoKey());
        profile.removePhotoKey();
    }

    private User getActiveUser(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
