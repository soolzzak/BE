package com.example.zzan.sse.service;

import com.example.zzan.follow.service.FollowService;
import com.example.zzan.room.entity.Room;
import com.example.zzan.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseService {
    private  FollowService followService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void notifyFollowers(Room room, String username) {
        String message = room.getHostUser().getUsername() + "님이 방을 만드셨습니다.";
        log.info(message + " - Sending SSE notification to followers.");

        this.followService.getFollowers(username).forEach(followerUsername -> {
            SseEmitter emitter = emitters.get(followerUsername);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name("roomCreated").data(message));
                } catch (IOException e) {
                    log.error("Error sending SSE event to follower: {}", followerUsername, e);
                    emitter.completeWithError(e);
                }
            }
        });
    }
}