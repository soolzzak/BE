package com.example.zzan.mypage.dto;


import com.example.zzan.user.entity.User;
import com.example.zzan.userHistory.dto.UserHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyPageResponseDto {

	private String userImageUrl;
	private String username;
	int alcohol;
	private List<UserHistoryDto> meetedUser;

	public MyPageResponseDto(User myPage,int alcohol,List<UserHistoryDto> meetedUser){
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
