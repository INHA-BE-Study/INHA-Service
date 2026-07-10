package org.study.inhamatch.domain.chat.service;

import org.study.inhamatch.domain.chat.dto.ChatRoomResponse;

// Matching 컨텍스트가 매칭 성립 시 호출하는 인터페이스
public interface ChatRoomCreator {

    ChatRoomResponse createRoom(Long matchId, Long userId1, Long userId2);
}