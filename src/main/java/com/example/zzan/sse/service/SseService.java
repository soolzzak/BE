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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseService {
    private FollowService followService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Autowired
    public SseService(FollowService followService) {
        this.followService = followService;
    }

    public void notifyFollowers(Room room, String username) {
        if (followService == null) {
            log.error("FollowService is null. Make sure it is properly initialized and injected.");
            return;
        }

        String message = room.getHostUser().getUsername() + "님이 방을 만드셨습니다.";
        log.info(message + " - Sending SSE notification to followers.");

        this.followService.getFollowers(username).forEach(followerUsername -> {
            SseEmitter emitter = emitters.get(followerUsername);
            log.info(followerUsername + "check if this is coming in please!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name("roomCreated").data(message));
                    log.info("roomCreated" + message + "메세지가 보내지나!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                } catch (IOException e) {
                    log.error("Error sending SSE event to follower: {}", followerUsername, e);
                    emitter.completeWithError(e);
                }
            }
        });
    }
}