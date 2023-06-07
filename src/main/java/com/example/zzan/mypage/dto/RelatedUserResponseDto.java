package com.example.zzan.mypage.dto;

import com.example.zzan.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RelatedUserResponseDto {

	private Long userId;
	private String userImage;
	private String username;
	private String email;
	private int alcohol;
	// private String reletedRoomName;


	public RelatedUserResponseDto(User myPage){
		this.userId = myPage.getId();
		this.userImage = myPage.getUserImage();
		this.username = myPage.getUsername();
		this.email = myPage.getEmail();
		this.alcohol =myPage.getAlcohol();
		// this.reletedRoomName = reletedRoomName;

	}

}
