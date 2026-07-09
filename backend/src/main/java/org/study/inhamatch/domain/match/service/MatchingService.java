package org.study.inhamatch.domain.match.service;

import lombok.RequiredArgsConstructor;
import org.study.inhamatch.domain.match.dto.MatchResultResponse;
import org.study.inhamatch.domain.match.entity.*;
import org.study.inhamatch.domain.match.repository.MatchRepository;
import org.study.inhamatch.domain.match.repository.MatchRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MatchRequestRepository matchRequestRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public void requestMatch(Long userId) {
        if (matchRequestRepository.existsByUserIdAndRequestDate(userId, LocalDate.now())) {
            throw new IllegalStateException("오늘 이미 매칭 요청을 했습니다.");
        }

        if (matchRepository.existsByUserIdAndStatusIn(userId, List.of(MatchStatus.PENDING, MatchStatus.ACCEPTED))) {
            throw new IllegalStateException("현재 진행 중인 매칭이 있습니다.");
        }

        matchRequestRepository.save(
                MatchRequest.builder()
                        .userId(userId)
                        .requestDate(LocalDate.now())
                        .build()
        );
    }

    @Transactional
    public void respondMatch(Long matchId, Long userId, boolean accepted) {
        Match match = matchRepository.findByIdAndUserBId(matchId, userId)
                .orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));

        if (accepted) {
            match.accept(null); // Chat 개발 후 chatRoomId 연동 예정
        } else {
            match.reject();
        }
    }

    public MatchResultResponse getMyMatch(Long userId) {
        Match match = matchRepository.findAllByUserIdAndStatus(userId, MatchStatus.PENDING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 매칭이 없습니다."));

        return MatchResultResponse.builder()
                .matchId(match.getId())
                .matchType(match.getMatchType())
                .status(match.getStatus())
                .matchedAt(match.getMatchedAt())
                .build();
    }
}