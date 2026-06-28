package org.study.inhamatch.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.inhamatch.domain.auth.entity.RefreshToken;
import org.study.inhamatch.domain.auth.entity.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteAllByUser(User user);
}
