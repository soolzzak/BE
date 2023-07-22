package com.example.zzan.sse.controller;

import com.example.zzan.sse.service.SseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Tag(name = "SseController", description = "SSE 파트")
@RequestMapping("/api/")
@RestController
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;
    private final Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @CrossOrigin
    @GetMapping(value = "/events/{username}", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(@PathVariable String username, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String accessKey = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessKey")) {
                    accessKey = cookie.getValue();
                    break;
                }
            }
        }

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseService.registerEmitter(username, sseEmitter);

        try {
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sseEmitter.onCompletion(() -> sseService.removeEmitter(username));
        sseEmitter.onTimeout(() -> sseService.removeEmitter(username));
        sseEmitter.onError((e) -> sseService.removeEmitter(username));

        return sseEmitter;
    }
}