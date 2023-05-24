package com.example.zzan.roomreport.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserReportDto {

	private String meetedUser;
	private LocalDateTime createdAt;

}
