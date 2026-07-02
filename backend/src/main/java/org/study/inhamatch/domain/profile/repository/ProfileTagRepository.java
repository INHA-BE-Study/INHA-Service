package org.study.inhamatch.domain.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.inhamatch.domain.profile.entity.Profile;
import org.study.inhamatch.domain.profile.entity.ProfileTag;

import java.util.List;

public interface ProfileTagRepository extends JpaRepository<ProfileTag, Long> {

    List<ProfileTag> findAllByProfile(Profile profile);

    void deleteAllByProfile(Profile profile);
}
