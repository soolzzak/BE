package com.example.zzan.roomreport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserReportDto {

	private String meetedUser;
	private LocalDateTime createdAt;

}
