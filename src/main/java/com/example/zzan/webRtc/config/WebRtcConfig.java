package com.example.zzan.webRtc.config;

import com.example.zzan.webRtc.rtc.SignalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.io.IOException;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebRtcConfig implements WebSocketConfigurer {

    private final SignalHandler signalHandler;
//    private final WebSocketConfig webSocketConfig;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalHandler, "/signal")
                .setAllowedOrigins("*");
    }

//    @Bean
//    public MusicGameService musicGameService() throws IOException {
//        return webSocketConfig.musicGameService();
//    }
}