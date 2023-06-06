package com.example.zzan.follow.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponseDto {
	private Long UserId;
	private String Username;
	private String Image;
	private LocalDateTime createdAt;
}
