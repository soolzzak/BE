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
        log.info(username + " 무엇일까요??????????????????????????????????????????????????");

        for (User follower : followers) {
            SseEmitter emitter = emitters.get(follower.getUsername());
            log.info(emitter +"   emitter 가 찍히는지 보고싶구나ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ");
            if (emitter != null) {
                try {
                    String message = room.getHostUser().getUsername() + "님이 방을 만드셨습니다.";
                    log.info(message + " message가 보여지는 구간ㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴ");
                    emitter.send(SseEmitter.event().name("roomCreated").data(message));
                    log.info("마지막으로" + emitter + "emiter가 보여지는 구간ㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴ");
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