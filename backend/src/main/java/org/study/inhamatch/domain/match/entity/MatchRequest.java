package org.study.inhamatch.domain.match.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "match_requests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "request_date"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public MatchRequest(Long userId, LocalDate requestDate) {
        this.userId = userId;
        this.requestDate = requestDate;
        this.status = RequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void updateStatus(RequestStatus status) {
        this.status = status;
    }
}