package org.study.inhamatch.domain.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.study.inhamatch.domain.chat.entity.ChatMessage;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {

    // 채팅방 전체 메시지 (오래된 순)
    Slice<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId, Pageable pageable);

    // 커서 기반: 특정 ID보다 작은(이전) 메시지 조회 (최신순으로 가져온 뒤 뒤집어서 사용)
    Slice<ChatMessage> findByRoomIdAndIdLessThanOrderByIdDesc(Long roomId, Long cursorId, Pageable pageable);
}
