package com.example.zzan.like.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.like.service.LikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "LikeController", description = "도수 파트")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {
    private final LikeService likeService;

    @PutMapping("/alcohol/like/{targetId}")
    public ResponseDto likeUser(@PathVariable Long targetId) {
        return likeService.increaseAlcohol(targetId, true);
    }

    @PutMapping("/alcohol/dislike/{targetId}")
    public ResponseDto dislikeUser(@PathVariable Long targetId) {
        return likeService.decreaseAlcohol(targetId, false);
    }
}