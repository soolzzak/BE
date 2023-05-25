package com.example.zzan.mypage.dto;


import com.example.zzan.roomreport.dto.UserReportDto;
import com.example.zzan.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyPageResponseDto {

	private String userImageUrl;
	private String username;
	int alcohol;
	private List<UserReportDto> meetedUser;

	public MyPageResponseDto(User myPage,int alcohol,List<UserReportDto> meetedUser){
		this.userImageUrl= myPage.getUserImage();
		this.username= myPage.getUsername();
		this.alcohol=alcohol;
		this.meetedUser=meetedUser;
	}

	public MyPageResponseDto(User myPage) {
		this.userImageUrl= myPage.getUserImage();
		this.username= myPage.getUsername();
	}
}
