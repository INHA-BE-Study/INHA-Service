package org.study.inhamatch.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.study.inhamatch.domain.chat.dto.ChatRoomResponse;
import org.study.inhamatch.domain.chat.dto.MessageHistoryResponse;
import org.study.inhamatch.domain.chat.service.ChatService;

import java.security.Principal;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 내 활성 채팅방 조회 — 채팅방 없으면 204
    @GetMapping("/rooms/me")
    public ResponseEntity<ChatRoomResponse> getMyActiveRoom(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return chatService.getActiveRoom(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // 메시지 이력 조회 — 커서 기반 페이지네이션
    // cursorId 없으면 최신 size개, 있으면 cursorId 이전 메시지
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<MessageHistoryResponse> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "30") int size,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        MessageHistoryResponse response = chatService.getMessageHistory(
                userId, roomId, cursorId, PageRequest.of(0, size));
        return ResponseEntity.ok(response);
    }

    // 채팅방 나가기 — 성공 시 204
    @PostMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable Long roomId, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        chatService.leaveRoom(userId, roomId);
        return ResponseEntity.noContent().build();
    }
}