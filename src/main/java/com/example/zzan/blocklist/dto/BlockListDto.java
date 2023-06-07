package com.example.zzan.blocklist.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlockListDto {
	private Long userId;
	private String username;
	private String userImage;
	private LocalDateTime createdAt;
}
