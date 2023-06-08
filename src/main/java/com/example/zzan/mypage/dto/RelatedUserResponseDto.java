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
	private String roomTitle;
	private int alcohol;
	private boolean alcoholUp;
	private boolean alcoholDown;
	private boolean follow;
	private boolean block;

	public RelatedUserResponseDto(User myPage, boolean follow, boolean block) {
		this.userId = myPage.getId();
		this.userImage = myPage.getUserImage();
		this.username = myPage.getUsername();
		this.email = myPage.getEmail();
		this.alcohol = myPage.getAlcohol();
		this.roomTitle = myPage.getRoomTitle();
		this.alcoholUp = myPage.isAlcoholUp();
		this.alcoholDown = myPage.isAlcoholDown();
		this.follow = follow;
		this.block = block;
	}
}
