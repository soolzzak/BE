package com.example.zzan.sse.controller;

import com.example.zzan.sse.service.SseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "BlockListController", description = "SSE 파트")
@RestController
public class SseController {
    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping("/events/{userId}")
    public SseEmitter handleSse(@PathVariable Long userId) {
        return this.sseService.register(userId);
    }

    @PostMapping("/room/{userId}")
    public String handleRoomCreation(@PathVariable Long userId) {
        this.sseService.notifyFollowers(userId);
        return "Room Created.";
    }
}
