package com.example.zzan.userHistory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserHistoryDto {
	private Long UserId;
	private String Username;
	private String Image;
	private LocalDateTime createdAt;
}
