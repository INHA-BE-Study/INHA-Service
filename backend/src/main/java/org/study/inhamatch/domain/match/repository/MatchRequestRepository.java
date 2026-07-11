package org.study.inhamatch.domain.match.repository;

import org.study.inhamatch.domain.match.entity.MatchRequest;
import org.study.inhamatch.domain.match.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    // 오늘 이미 요청했는지 체크
    boolean existsByUserIdAndRequestDate(Long userId, LocalDate requestDate);

    // 스케줄러가 PENDING 요청 전체 조회
    List<MatchRequest> findAllByStatusAndRequestDate(RequestStatus status, LocalDate requestDate);

    // 내 요청 조회
    Optional<MatchRequest> findByUserIdAndRequestDate(Long userId, LocalDate requestDate);
}