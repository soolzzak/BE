package com.example.zzan.sse.controller;

import com.example.zzan.global.jwt.JwtUtil;
import com.example.zzan.sse.service.SseService;
import com.example.zzan.user.entity.User;
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
    public static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();


    @CrossOrigin
    @GetMapping(value = "/events/{username}", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(@PathVariable String username, HttpServletRequest request) {

        String nickname = getAccessTokenFromCookie(request);
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try{
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sseEmitters.put(username,sseEmitter);

        sseEmitter.onCompletion(()->sseEmitters.remove(nickname));
        sseEmitter.onTimeout(()->sseEmitters.remove(nickname));
        sseEmitter.onError((e)-> sseEmitters.remove(nickname));

        return sseEmitter;
    }

    private String getAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessKey")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}