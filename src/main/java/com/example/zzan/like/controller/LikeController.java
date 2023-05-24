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
@RequestMapping("/user")
public class LikeController {

    private final LikeService likeService;

    @PutMapping("/{userId}/like")
    public ResponseDto<String> likeUser(@PathVariable Long userId) {
        likeService.updateAlcohol(userId, true);
        return ResponseDto.setSuccess("도수를 올렸습니다.");
    }

    @PutMapping("/{userId}/dislike")
    public ResponseDto<String> dislikeUser(@PathVariable Long userId) {
        likeService.updateAlcohol(userId, false);
        return ResponseDto.setSuccess("도수를 내렸습니다.");
    }
}
