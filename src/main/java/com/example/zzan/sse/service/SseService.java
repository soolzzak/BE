package com.example.zzan.sse.service;

import com.example.zzan.follow.service.FollowService;
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
    private final Map<User, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(User username) {
        SseEmitter emitter = new SseEmitter();

        emitter.onCompletion(() -> {
            this.emitters.remove(username);
        });

        emitter.onError((e) -> {
            this.emitters.remove(username);
        });

        this.emitters.put(username, emitter);

        return emitter;
    }

    public void notifyFollowers(String username) {
        this.followService.getFollowers(username).forEach(followerUsername -> {
            SseEmitter emitter = emitters.get(followerUsername);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name("roomCreated").data(username + "님이 방을 만드셨습니다."));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        });
    }
}