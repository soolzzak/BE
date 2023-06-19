package com.example.zzan.sse.service;

import com.example.zzan.follow.service.FollowService;
import com.example.zzan.room.entity.Room;
import com.example.zzan.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SseService {
    private final FollowService followService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void notifyFollowers(Room room, String username) {
        String message = room.getHostUser().getUsername() + "님이 방을 만드셨습니다.";
        this.followService.getFollowers(username).forEach(followerUsername -> {
            SseEmitter emitter = emitters.get(followerUsername);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name("roomCreated").data(message));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        });
    }
}