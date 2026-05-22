package org.study.inhamatch.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.inhamatch.domain.auth.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
