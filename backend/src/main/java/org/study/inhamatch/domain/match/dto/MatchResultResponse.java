package org.study.inhamatch.domain.match.dto;

import lombok.Builder;
import lombok.Getter;
import org.study.inhamatch.domain.match.entity.MatchStatus;
import org.study.inhamatch.domain.match.entity.MatchType;

import java.time.LocalDateTime;

@Getter
@Builder
public class MatchResultResponse {
    private Long matchId;
    private MatchType matchType;
    private MatchStatus status;
    private LocalDateTime matchedAt;
}