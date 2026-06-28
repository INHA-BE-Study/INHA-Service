package org.study.inhamatch.domain.match.controller;

import lombok.RequiredArgsConstructor;
import org.study.inhamatch.domain.match.dto.MatchRespondRequest;
import org.study.inhamatch.domain.match.dto.MatchResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.study.inhamatch.domain.match.service.MatchingService;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    // 요청하기
    @PostMapping("/request")
    public ResponseEntity<Void> requestMatch(@RequestAttribute Long userId) {
        matchingService.requestMatch(userId);
        return ResponseEntity.ok().build();
    }

    // 수락/거절
    @PostMapping("/respond")
    public ResponseEntity<Void> respondMatch(
            @RequestAttribute Long userId,
            @RequestBody MatchRespondRequest request
    ) {
        matchingService.respondMatch(request.getMatchId(), userId, request.isAccepted());
        return ResponseEntity.ok().build();
    }

    // 내 매칭 상태 조회
    @GetMapping("/status")
    public ResponseEntity<MatchResultResponse> getMyMatch(@RequestAttribute Long userId) {
        return ResponseEntity.ok(matchingService.getMyMatch(userId));
    }
}