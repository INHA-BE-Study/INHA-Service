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
import java.util.HashSet;
import java.util.Set;

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
        Set<Long> matchedUserIds = new HashSet<>();

        for (MatchRequest request : requests) {
            Long userId = request.getUserId();

            if (matchedUserIds.contains(userId)) continue;
            if (request.getStatus() != RequestStatus.PENDING) continue;

            MatchRequest partner = requests.stream()
                    .filter(r -> !r.getUserId().equals(userId))
                    .filter(r -> r.getStatus() == RequestStatus.PENDING)
                    .filter(r -> !matchedUserIds.contains(r.getUserId()))
                    .findFirst()
                    .orElse(null);

            if (partner == null) continue;

            matchRepository.save(
                    Match.builder()
                            .userAId(userId)
                            .userBId(partner.getUserId())
                            .matchType(MatchType.MUTUAL_REQUEST)
                            .build()
            );

            request.updateStatus(RequestStatus.MATCHED);
            partner.updateStatus(RequestStatus.MATCHED);

            matchedUserIds.add(userId);
            matchedUserIds.add(partner.getUserId());
        }
    }

    private void expireUnmatchedRequests(List<MatchRequest> requests) {
        requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .forEach(r -> r.updateStatus(RequestStatus.EXPIRED));
    }
}