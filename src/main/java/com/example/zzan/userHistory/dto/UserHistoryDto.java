package com.example.zzan.userHistory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserHistoryDto {
	private Long userId;
	private String username;
	private String userImage;
	private LocalDateTime createdAt;
}
