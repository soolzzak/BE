package com.example.zzan.blocklist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BlockListDto {
	private Long userId;
	private String username;
	private String userImage;
	private LocalDateTime createdAt;
}
