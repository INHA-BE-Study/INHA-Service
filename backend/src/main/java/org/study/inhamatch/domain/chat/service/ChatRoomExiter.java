package org.study.inhamatch.domain.chat.service;

// Report 컨텍스트가 신고 시 자동으로 채팅방 나가기를 호출하는 인터페이스
public interface ChatRoomExiter {

    void leaveRoom(Long userId, Long roomId);
}