package org.study.inhamatch.domain.chat.repository;

import org.study.inhamatch.domain.chat.entity.ChatRoom;

import java.util.Optional;
import java.util.List;

public interface ChatRoomRepository {

    ChatRoom save(ChatRoom chatRoom);

    Optional<ChatRoom> findById(Long id);

    // 매칭 ID로 채팅방 조회 (매칭 연동 시 사용)
    Optional<ChatRoom> findByMatchId(Long matchId);

    // 특정 유저가 속한 채팅방 목록 조회
    List<ChatRoom> findAllByUserId(Long userId);

    // 특정 유저가 속한 활성 채팅방 존재 여부 (새 매칭 가능 여부 체크용)
    boolean existsByUserId(Long userId);
}
