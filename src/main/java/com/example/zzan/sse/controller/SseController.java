package com.example.zzan.sse.controller;

import com.example.zzan.sse.service.SseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "SseController", description = "SSE 파트")
@RequestMapping("/api/")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;

    @GetMapping("/events/{username}")
    public SseEmitter handleSse(@PathVariable String username) {
        return this.sseService.register(username);
    }

    @PostMapping("/room/{username}")
    public String handleRoomCreation(@PathVariable String username) {
        this.sseService.notifyFollowers(username);
        return username + "created a Room.";
    }
}
