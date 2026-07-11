package org.study.inhamatch.domain.match.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userAId;

    @Column(nullable = false)
    private Long userBId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchType matchType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime matchedAt;

    private LocalDateTime respondedAt;  // 수락/거절 시각 (nullable)

    private Long chatRoomId;            // 수락 시 채팅방 ID (nullable)

    @Builder
    public Match(Long userAId, Long userBId, MatchType matchType) {
        this.userAId = userAId;
        this.userBId = userBId;
        this.matchType = matchType;
        this.status = MatchStatus.PENDING;
        this.matchedAt = LocalDateTime.now();
    }

    public void accept(Long chatRoomId) {
        this.status = MatchStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
        this.chatRoomId = chatRoomId;
    }

    public void reject() {
        this.status = MatchStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = MatchStatus.EXPIRED;
    }
}
