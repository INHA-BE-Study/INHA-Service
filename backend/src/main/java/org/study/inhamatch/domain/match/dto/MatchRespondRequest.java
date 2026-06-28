package org.study.inhamatch.domain.match.dto;

import lombok.Getter;

@Getter
public class MatchRespondRequest {
    private Long matchId;
    private boolean accepted;
}