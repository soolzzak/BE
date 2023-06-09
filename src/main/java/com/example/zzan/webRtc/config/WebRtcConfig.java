package com.example.zzan.webRtc.config;

import java.io.IOException;

import com.example.zzan.webRtc.rtc.SignalHandler;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebRtcConfig implements WebSocketConfigurer {
    private final SignalHandler signalHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalHandler, "/signal")
            .setAllowedOrigins("*")
            .withSockJS()
            .setHeartbeatTime(40000);
    }
    // @Bean
    // public ServletServerContainerFactoryBean createWebSocketContainer() {
    //     ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    //     container.setMaxSessionIdleTimeout(500000L); // 500,000 milliseconds (8.33 minutes)
    //     return container;
    // }

}
