package org.study.inhamatch.domain.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.study.inhamatch.domain.chat.entity.ChatMessage;

public interface ChatMessageRepository {

    ChatMessage save(ChatMessage chatMessage);

    // 채팅방 메시지 페이지네이션 (오래된 순, 무한 스크롤용 Slice)
    Slice<ChatMessage> findByRoomId(Long roomId, Pageable pageable);

    // 특정 메시지 ID 이전 메시지 조회 (커서 기반 페이지네이션)
    Slice<ChatMessage> findByRoomIdAndIdLessThan(Long roomId, Long cursorId, Pageable pageable);
}
