package com.example.zzan.sse.service;

import com.example.zzan.follow.service.FollowService;
import com.example.zzan.room.entity.Room;
import com.example.zzan.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
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
                    String message = room.getHostUser().getUsername();
                    String roomId = room.getId().toString();

                    Map<String, String> eventData = new HashMap<>();
                    eventData.put("message", message);
                    eventData.put("roomId", roomId);

                    emitter.send(SseEmitter.event().name("roomCreated").data(eventData));
                    log.info(eventData.toString());
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }
    }

    public void sendSseMessage(String username, String message) {
        SseEmitter emitter = emitters.get(username);
        if (emitter == null) {
            emitter = new SseEmitter();
            emitters.put(username, emitter);
        }

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(message));
                log.info("SSE message sent to user: {}", username);
            } catch (IOException e) {
                emitter.completeWithError(e);
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