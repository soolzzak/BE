package com.example.zzan.userHistory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserHistoryDto {

	private String metUser;
	private LocalDateTime createdAt;
	private String metUserImage;

}
