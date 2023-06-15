package com.example.zzan.webRtc.rtc;

import com.example.zzan.game.MusicGameService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class GameHandler extends TextWebSocketHandler {

    private MusicGameService musicGameService;

    public GameHandler(MusicGameService musicGameService) {
        this.musicGameService = musicGameService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        // Perform any necessary initialization or handling when a new WebSocket connection is established
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming text messages from WebSocket clients
        String payload = message.getPayload();

        if ("startGame".equals(payload)) {
            // Start the music guessing game
            musicGameService.startGame(session);
        } else {
            // Handle other message types or game-related logic
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        // Perform any necessary cleanup or handling when a WebSocket connection is closed
    }
}