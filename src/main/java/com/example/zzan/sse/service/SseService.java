package com.example.zzan.sse.service;

import com.example.zzan.follow.service.FollowService;
import com.example.zzan.room.entity.Room;
import com.example.zzan.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseService {
    private final FollowService followService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Autowired
    public SseService(FollowService followService) {
        this.followService = followService;
    }

    public void notifyFollowers(Room room, String username) {
        List<User> followers = followService.getFollowers(username);

        for (User follower : followers) {
            SseEmitter emitter = emitters.get(follower.getUsername());
            if (emitter != null) {
                try {
                    String message = room.getHostUser().getUsername() + "님이 방을 만드셨습니다.";
                    log.info(room.getHostUser().getUsername() + "가 들어오면 notifyFollowers 진입 성공");
                    emitter.send(SseEmitter.event().name("roomCreated").data(message));
                    log.info("여기가 들어오면" + message + "보내는것도 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }
    }
    public void registerEmitter(String username, SseEmitter emitter) {
        emitters.put(username, emitter);
        log.info("SSE emitter registered for user: {}", username);
    }

    public void removeEmitter(String username) {
        emitters.remove(username);
        log.info("SSE emitter removed for user: {}", username);
    }
}