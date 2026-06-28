package org.study.inhamatch.domain.match.service;

import lombok.RequiredArgsConstructor;
import org.study.inhamatch.domain.match.entity.*;
import org.study.inhamatch.domain.match.repository.MatchRepository;
import org.study.inhamatch.domain.match.repository.MatchRequestRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchingScheduler {

    private final MatchRequestRepository matchRequestRepository;
    private final MatchRepository matchRepository;
    private final MatchingService matchingService;

    // 매일 오후 6시 1분 마감
    @Scheduled(cron = "0 1 18 * * *")
    @Transactional
    public void processMatching() {
        List<MatchRequest> pendingRequests = matchRequestRepository
                .findAllByStatusAndRequestDate(RequestStatus.PENDING, LocalDate.now());

        // 1순위: 둘 다 요청한 쌍 (MUTUAL_REQUEST) 먼저 매칭
        processMutualRequests(pendingRequests);

        // 2순위: 남은 요청자에게 미요청자 알림 (NOTIFY) - 추후 구현
        // processNotifyRequests(pendingRequests);

        // 매칭 안 된 요청 EXPIRED 처리
        expireUnmatchedRequests(pendingRequests);
    }

    private void processMutualRequests(List<MatchRequest> requests) {
        List<Long> requestUserIds = requests.stream()
                .map(MatchRequest::getUserId)
                .toList();

        for (MatchRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) continue;

            // 상대방도 요청했는지 확인
            // 하드 필터링 (이성, 차단 상대 등) - Report 개발 후 추가
            Long matchedUserId = requestUserIds.stream()
                    .filter(id -> !id.equals(request.getUserId()))
                    .findFirst()
                    .orElse(null);

            if (matchedUserId == null) continue;

            matchRepository.save(
                    Match.builder()
                            .userAId(request.getUserId())
                            .userBId(matchedUserId)
                            .matchType(MatchType.MUTUAL_REQUEST)
                            .build()
            );

            request.updateStatus(RequestStatus.MATCHED);
        }
    }

    private void expireUnmatchedRequests(List<MatchRequest> requests) {
        requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .forEach(r -> r.updateStatus(RequestStatus.EXPIRED));
    }
}