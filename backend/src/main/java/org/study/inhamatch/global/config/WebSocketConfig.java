package org.study.inhamatch.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")          // 프론트가 WebSocket 연결할 주소
                .setAllowedOriginPatterns("*") // CORS (나중에 프론트 도메인으로 교체)
                .withSockJS();               // SockJS 폴백 (WebSocket 미지원 환경 대비)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");         // 서버 → 클라이언트 구독 prefix
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트 → 서버 전송 prefix
    }
}
