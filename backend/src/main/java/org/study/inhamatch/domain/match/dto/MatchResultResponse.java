package org.study.inhamatch.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.study.inhamatch.domain.match.entity.MatchStatus;
import org.study.inhamatch.domain.match.entity.MatchType;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultResponse {
    private Long matchId;
    private MatchType matchType;
    private MatchStatus status;
    private LocalDateTime matchedAt;
}