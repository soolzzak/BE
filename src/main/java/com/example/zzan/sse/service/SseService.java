package com.example.zzan.sse.service;

import com.example.zzan.follow.service.FollowService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private final FollowService followService;

    private Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseService(FollowService followService) {
        this.followService = followService;
    }

    public SseEmitter register(Long userId) {
        SseEmitter emitter = new SseEmitter();

        emitter.onCompletion(() -> this.emitters.remove(userId));
        emitter.onError((e) -> this.emitters.remove(userId));

        this.emitters.put(userId, emitter);

        return emitter;
    }

    public void notifyFollowers(String username) {
        this.followService.getFollowers(username).forEach(followerId -> {
            SseEmitter emitter = emitters.get(username);
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
