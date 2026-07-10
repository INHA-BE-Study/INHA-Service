package org.study.inhamatch.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.inhamatch.domain.chat.dto.ChatRoomResponse;
import org.study.inhamatch.domain.chat.dto.MessageHistoryResponse;
import org.study.inhamatch.domain.chat.dto.MessageResponse;
import org.study.inhamatch.domain.chat.entity.ChatMessage;
import org.study.inhamatch.domain.chat.entity.ChatMessageType;
import org.study.inhamatch.domain.chat.entity.ChatRoom;
import org.study.inhamatch.domain.chat.entity.ChatRoomMember;
import org.study.inhamatch.domain.chat.repository.ChatMessageRepository;
import org.study.inhamatch.domain.chat.repository.ChatRoomRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService implements ChatRoomCreator, ChatRoomExiter {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 매칭 성립 시 호출 — matchId로 중복 방지
    public ChatRoomResponse createRoom(Long matchId, Long userId1, Long userId2) {
        if (chatRoomRepository.findByMatchId(matchId).isPresent()) {
            throw new IllegalStateException("이미 해당 매칭에 대한 채팅방이 존재합니다.");
        }

        ChatRoom room = ChatRoom.create(matchId);
        room.getMembers().add(ChatRoomMember.create(room, userId1));
        room.getMembers().add(ChatRoomMember.create(room, userId2));

        return ChatRoomResponse.from(chatRoomRepository.save(room));
    }

    // WebSocket 핸들러에서 호출 — 메시지 저장
    public MessageResponse saveMessage(Long userId, Long roomId, String body, ChatMessageType type) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. roomId=" + roomId));

        if (room.isClosed()) {
            throw new IllegalStateException("종료된 채팅방입니다.");
        }

        boolean isMember = room.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId) && !m.hasLeft());
        if (!isMember) {
            throw new IllegalArgumentException("채팅방 참여자가 아닙니다.");
        }

        return MessageResponse.from(chatMessageRepository.save(
                ChatMessage.create(room, userId, body, type)));
    }

    // 메시지 이력 조회 — 커서 기반 페이지네이션 (최신순 DESC)
    // cursorId=null이면 가장 최근 N개, 이후 요청은 이전 응답의 nextCursorId 사용
    @Transactional(readOnly = true)
    public MessageHistoryResponse getMessageHistory(Long userId, Long roomId, Long cursorId, Pageable pageable) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. roomId=" + roomId));

        boolean isMember = room.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));
        if (!isMember) {
            throw new IllegalArgumentException("채팅방 참여자가 아닙니다.");
        }

        Long effectiveCursor = cursorId != null ? cursorId : Long.MAX_VALUE;
        Slice<ChatMessage> slice = chatMessageRepository.findByRoomIdAndIdLessThan(roomId, effectiveCursor, pageable);

        List<MessageResponse> messages = slice.getContent().stream()
                .map(MessageResponse::from)
                .toList();

        return MessageHistoryResponse.of(messages, slice.hasNext());
    }

    // 채팅방 나가기 — 한쪽이라도 나가면 방 폐기 (정책: 양쪽 모두 내용 확인 불가)
    public void leaveRoom(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. roomId=" + roomId));

        if (room.isClosed()) {
            throw new IllegalStateException("이미 종료된 채팅방입니다.");
        }

        ChatRoomMember member = room.getMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("채팅방 참여자가 아닙니다."));

        member.leave();
        room.close();
        chatRoomRepository.save(room);
    }

    // 내 활성 채팅방 조회 — 새 매칭 가능 여부 판단에도 사용
    @Transactional(readOnly = true)
    public Optional<ChatRoomResponse> getActiveRoom(Long userId) {
        return chatRoomRepository.findAllByUserId(userId).stream()
                .filter(r -> !r.isClosed())
                .findFirst()
                .map(ChatRoomResponse::from);
    }
}