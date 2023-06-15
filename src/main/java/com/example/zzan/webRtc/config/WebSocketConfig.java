package com.example.zzan.webRtc.config;

import com.example.zzan.game.MusicGameService;
import com.example.zzan.webRtc.rtc.GameHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.io.IOException;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MusicGameService musicGameService;

    public WebSocketConfig(MusicGameService musicGameService) {
        this.musicGameService = musicGameService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameHandler(), "/game")
                .setAllowedOrigins("*");
    }

    @Bean
    public GameHandler gameHandler() {
        return new GameHandler(musicGameService);
    }

    @Bean
    public MusicGameService musicGameService() throws IOException {
        return new MusicGameService("path/to/your/file.txt");
    }
}