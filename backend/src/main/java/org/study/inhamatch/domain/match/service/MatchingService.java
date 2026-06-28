package org.study.inhamatch.domain.match.service;

import lombok.RequiredArgsConstructor;
import org.study.inhamatch.domain.match.entity.*;
import org.study.inhamatch.domain.match.repository.MatchRepository;
import org.study.inhamatch.domain.match.repository.MatchRequestRepository;
import org.study.inhamatch.domain.match.dto.MatchResultResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MatchRequestRepository matchRequestRepository;
    private final MatchRepository matchRepository;

    // 요청하기
    @Transactional
    public void requestMatch(Long userId) {
        // 1. 오늘 이미 요청했는지 체크
        if (matchRequestRepository.existsByUserIdAndRequestDate(userId, LocalDate.now())) {
            throw new IllegalStateException("오늘 이미 매칭 요청을 했습니다.");
        }

        // 2. 현재 활성 매칭(채팅방)이 있는지 체크
        if (matchRepository.existsByUserAIdOrUserBIdAndStatus(userId, userId, MatchStatus.ACCEPTED)) {
            throw new IllegalStateException("현재 진행 중인 매칭이 있습니다.");
        }

        // 3. 요청 저장
        matchRequestRepository.save(
                MatchRequest.builder()
                        .userId(userId)
                        .requestDate(LocalDate.now())
                        .build()
        );
    }

    // 수락/거절
    @Transactional
    public void respondMatch(Long matchId, Long userId, boolean accepted) {
        Match match = matchRepository.findByIdAndUserBId(matchId, userId)
                .orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));

        if (accepted) {
            // Chat에 채팅방 생성 요청 (Chat 개발 후 연동)
            // Long chatRoomId = chatService.createChatRoom(match.getUserAId(), match.getUserBId());
            // match.accept(chatRoomId);
            match.accept(null); // 임시
        } else {
            match.reject();
        }
    }

    // 내 매칭 상태 조회
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