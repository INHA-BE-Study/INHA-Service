package org.study.inhamatch.domain.chat.dto;

import org.study.inhamatch.domain.chat.entity.ChatMessage;
import org.study.inhamatch.domain.chat.entity.ChatMessageType;

import java.time.LocalDateTime;

public record MessageResponse(
        Long messageId,
        Long roomId,
        Long senderUserId,
        String body,
        ChatMessageType type,
        LocalDateTime createdAt
) {
    public static MessageResponse from(ChatMessage message) {
        return new MessageResponse(
                message.getId(),
                message.getRoom().getId(),
                message.getSenderUserId(),
                message.getBody(),
                message.getType(),
                message.getCreatedAt()
        );
    }
}
