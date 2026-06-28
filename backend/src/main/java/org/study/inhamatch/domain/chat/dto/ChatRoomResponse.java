package org.study.inhamatch.domain.chat.dto;

import org.study.inhamatch.domain.chat.entity.ChatRoom;
import org.study.inhamatch.domain.chat.entity.ChatRoomMember;

import java.time.LocalDateTime;
import java.util.List;

public record ChatRoomResponse(
        Long roomId,
        Long matchId,
        List<Long> memberUserIds,
        LocalDateTime createdAt
) {
    public static ChatRoomResponse from(ChatRoom chatRoom) {
        List<Long> memberUserIds = chatRoom.getMembers().stream()
                .map(ChatRoomMember::getUserId)
                .toList();

        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getMatchId(),
                memberUserIds,
                chatRoom.getCreatedAt()
        );
    }
}
