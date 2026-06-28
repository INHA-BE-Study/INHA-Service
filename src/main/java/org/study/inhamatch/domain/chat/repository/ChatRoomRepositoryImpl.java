package org.study.inhamatch.domain.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.study.inhamatch.domain.chat.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomJpaRepository.save(chatRoom);
    }

    @Override
    public Optional<ChatRoom> findById(Long id) {
        return chatRoomJpaRepository.findById(id);
    }

    @Override
    public Optional<ChatRoom> findByMatchId(Long matchId) {
        return chatRoomJpaRepository.findByMatchId(matchId);
    }

    @Override
    public List<ChatRoom> findAllByUserId(Long userId) {
        return chatRoomJpaRepository.findAllByUserId(userId);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return chatRoomJpaRepository.existsByUserId(userId);
    }
}
