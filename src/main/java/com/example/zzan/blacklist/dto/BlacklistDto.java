package com.example.zzan.blacklist.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlacklistDto {
	private Long UserId;
	private String Username;
	private String Image;
	private LocalDateTime createdAt;
}
