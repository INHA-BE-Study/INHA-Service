package org.study.inhamatch.domain.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.study.inhamatch.domain.chat.entity.ChatMessage;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final ChatMessageJpaRepository chatMessageJpaRepository;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageJpaRepository.save(chatMessage);
    }

    @Override
    public Slice<ChatMessage> findByRoomId(Long roomId, Pageable pageable) {
        return chatMessageJpaRepository.findByRoomIdOrderByCreatedAtAsc(roomId, pageable);
    }

    @Override
    public Slice<ChatMessage> findByRoomIdAndIdLessThan(Long roomId, Long cursorId, Pageable pageable) {
        return chatMessageJpaRepository.findByRoomIdAndIdLessThanOrderByIdDesc(roomId, cursorId, pageable);
    }
}
