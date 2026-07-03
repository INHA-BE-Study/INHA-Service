package org.study.inhamatch.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_messages",
        indexes = @Index(name = "idx_chat_messages_room_created", columnList = "chat_room_id, created_at")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom room;

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ChatMessageType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static ChatMessage create(ChatRoom room, Long senderUserId, String body, ChatMessageType type) {
        return ChatMessage.builder()
                .room(room)
                .senderUserId(senderUserId)
                .body(body)
                .type(type != null ? type : ChatMessageType.TEXT)
                .createdAt(LocalDateTime.now())
                .build();
    }
}