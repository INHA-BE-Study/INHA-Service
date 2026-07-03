package org.study.inhamatch.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_room_members",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_chat_room_member_room_user",
                columnNames = {"chat_room_id", "user_id"}
        ),
        indexes = {
                @Index(name = "idx_chat_room_members_user_id", columnList = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom room;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    public static ChatRoomMember create(ChatRoom room, Long userId) {
        return ChatRoomMember.builder()
                .room(room)
                .userId(userId)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public boolean hasLeft() {
        return leftAt != null;
    }

    public void leave() {
        if (this.leftAt == null) {
            this.leftAt = LocalDateTime.now();
        }
    }

    public void markRead(Long messageId) {
        this.lastReadMessageId = messageId;
    }
}