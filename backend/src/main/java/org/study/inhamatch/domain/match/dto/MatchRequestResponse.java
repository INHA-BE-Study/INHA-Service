package org.study.inhamatch.domain.match.dto;

import lombok.Builder;
import lombok.Getter;
import org.study.inhamatch.domain.match.entity.RequestStatus;

import java.time.LocalDate;

@Getter
@Builder
public class MatchRequestResponse {
    private Long requestId;
    private RequestStatus status;
    private LocalDate requestDate;
}
