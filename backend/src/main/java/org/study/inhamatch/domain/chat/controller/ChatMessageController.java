package org.study.inhamatch.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.study.inhamatch.domain.chat.dto.MessageRequest;
import org.study.inhamatch.domain.chat.dto.MessageResponse;
import org.study.inhamatch.domain.chat.service.ChatService;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트 → 서버: /app/chat.send
     * 서버 → 구독자: /topic/chat/{roomId}
     *
     * principal.getName()은 JWT ChannelInterceptor가 STOMP CONNECT 헤더의
     * Authorization 토큰을 파싱해 userId(Long)를 subject로 설정한 뒤 동작한다.
     * → Auth 팀의 JWT 설정 완료 후 연동 가능.
     */
    @MessageMapping("/chat.send")
    public void send(@Validated @Payload MessageRequest request, Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        MessageResponse response = chatService.saveMessage(
                userId,
                request.roomId(),
                request.body(),
                request.resolvedType()
        );

        messagingTemplate.convertAndSend("/topic/chat/" + request.roomId(), response);
    }

    // 메시지 처리 중 발생한 예외를 해당 유저에게만 전달
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public Map<String, String> handleException(Exception e) {
        return Map.of("error", e.getMessage());
    }
}