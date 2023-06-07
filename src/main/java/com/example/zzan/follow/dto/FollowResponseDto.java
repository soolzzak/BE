package com.example.zzan.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FollowResponseDto {
	private Long UserId;
	private String Username;
	private String Image;
	private LocalDateTime createdAt;
}
