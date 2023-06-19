package com.example.zzan.sse.service;

import com.example.zzan.follow.service.FollowService;
import com.example.zzan.room.entity.Room;
import com.example.zzan.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {
    private final FollowService followService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void notifyFollowers(Room room, String username) {
        List<User> followers = followService.getFollowers(username);

        for (User follower : followers) {
            SseEmitter emitter = emitters.get(follower.getUsername());
            if (emitter == null) {
                emitter = new SseEmitter();
                emitters.put(follower.getUsername(), emitter);
            }
            if (emitter != null) {
                try {
                    String message = room.getHostUser().getUsername() + "님이" + room.getId() + "방을 만드셨습니다.";
                    emitter.send(SseEmitter.event().name("roomCreated").data(message));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }
    }

    public void registerEmitter(String username, SseEmitter emitter) {
        emitters.put(username, emitter);
    }

    public void removeEmitter(String username) {
        emitters.remove(username);
    }
}