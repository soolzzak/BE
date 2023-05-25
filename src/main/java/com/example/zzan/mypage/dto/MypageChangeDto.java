package com.example.zzan.mypage.dto;

import com.example.zzan.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MypageChangeDto {


	private String userImageUrl;
	private String username;


	public MypageChangeDto(User myPage) {
		this.userImageUrl= myPage.getUserImage();
		this.username= myPage.getUsername();
	}

}
