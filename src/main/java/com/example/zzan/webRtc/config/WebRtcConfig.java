package com.example.zzan.webRtc.config;

import com.example.zzan.webRtc.rtc.SignalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebRtcConfig implements WebSocketConfigurer {
    private final SignalHandler signalHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalHandler, "/signal")
                .setAllowedOrigins("*");
    }


    // 웹 소켓에서 rtc 통신을 위한 최대 텍스트 버퍼와 바이너리 버퍼 사이즈를 설정한다?
    // @Bean
    // public ServletServerContainerFactoryBean createWebSocketContainer() {
    //     ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    //     container.setMaxTextMessageBufferSize(8192);
    //     container.setMaxBinaryMessageBufferSize(8192);
    //     return container;
    // }

}
