package org.study.inhamatch.domain.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.inhamatch.domain.profile.entity.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);
}
