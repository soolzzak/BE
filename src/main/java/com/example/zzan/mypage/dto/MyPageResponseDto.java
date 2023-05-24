package com.example.zzan.mypage.dto;


import com.example.zzan.roomreport.dto.UserReportDto;
import com.example.zzan.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyPageResponseDto {

	private String imgUrl;
	private String username;
	int alcolhol;
	private List<UserReportDto> meetedUser;

	public MyPageResponseDto(User myPage,int alcolhol,List<UserReportDto> meetedUser){
		this.imgUrl= myPage.getImg();
		this.username= myPage.getUsername();
		this.alcolhol=alcolhol;
		this.meetedUser=meetedUser;
	}

	public MyPageResponseDto(User myPage) {
		this.imgUrl= myPage.getImg();
		this.username= myPage.getUsername();
	}
}
