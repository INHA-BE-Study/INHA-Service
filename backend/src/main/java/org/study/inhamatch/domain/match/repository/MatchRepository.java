package org.study.inhamatch.domain.match.repository;

import org.study.inhamatch.domain.match.entity.Match;
import org.study.inhamatch.domain.match.entity.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    // 현재 활성 매칭 있는지 체크 (채팅방 있는 사람 필터링)
    @Query("SELECT COUNT(m) > 0 FROM Match m WHERE (m.userAId = :userId OR m.userBId = :userId) AND m.status IN :statuses")
    boolean existsByUserIdAndStatusIn(@Param("userId") Long userId, @Param("statuses") List<MatchStatus> statuses);

    // 기매칭 상대 조회 (하드 필터링용)
    @Query("SELECT m FROM Match m WHERE (m.userAId = :userId OR m.userBId = :userId) AND m.status = :status")
    List<Match> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") MatchStatus status);

    // 매칭 단건 조회
    Optional<Match> findByIdAndUserBId(Long matchId, Long userBId);
}