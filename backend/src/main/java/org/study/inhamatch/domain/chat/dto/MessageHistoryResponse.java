package org.study.inhamatch.domain.chat.dto;

import java.util.List;

// 메시지 이전 내역 조회 응답 (커서 기반 페이지네이션)
public record MessageHistoryResponse(
        List<MessageResponse> messages,

        // 다음 페이지 요청 시 사용할 커서 (가장 오래된 메시지 ID)
        // null이면 더 이상 이전 메시지 없음
        Long nextCursorId,

        boolean hasNext
) {
    public static MessageHistoryResponse of(List<MessageResponse> messages, boolean hasNext) {
        Long nextCursorId = null;
        if (hasNext && !messages.isEmpty()) {
            nextCursorId = messages.get(messages.size() - 1).messageId();
        }
        return new MessageHistoryResponse(messages, nextCursorId, hasNext);
    }
}
