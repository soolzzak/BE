package com.example.zzan.like.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    @PutMapping("/alcohol/like/{targetId}")
    public ResponseDto likeUser(@PathVariable Long targetId) {
        return likeService.updateAlcohol(targetId, true);
    }

    @PutMapping("/alcohol/dislike/{targetId}")
    public ResponseDto dislikeUser(@PathVariable Long targetId) {
        return likeService.updateAlcohol(targetId, false);
    }
}