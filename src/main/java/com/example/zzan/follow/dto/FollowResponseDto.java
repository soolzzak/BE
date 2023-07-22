package com.example.zzan.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FollowResponseDto {
    private Long userId;
    private String username;
    private String userImage;
    private LocalDateTime createdAt;
}
