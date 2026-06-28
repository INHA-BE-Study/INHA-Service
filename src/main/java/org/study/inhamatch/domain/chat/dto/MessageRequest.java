package org.study.inhamatch.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.study.inhamatch.domain.chat.entity.ChatMessageType;

public record MessageRequest(

        @NotNull(message = "채팅방 ID는 필수입니다.")
        Long roomId,

        @NotBlank(message = "메시지 내용은 비어있을 수 없습니다.")
        String body,

        // 기본값 TEXT, IMAGE·SYSTEM 타입 확장 대비
        ChatMessageType type
) {
    // type이 null이면 TEXT로 기본값 처리
    public ChatMessageType resolvedType() {
        return type != null ? type : ChatMessageType.TEXT;
    }
}
