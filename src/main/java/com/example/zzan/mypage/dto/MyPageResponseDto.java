package com.example.zzan.mypage.dto;


import com.example.zzan.blocklist.dto.BlockListDto;
import com.example.zzan.follow.dto.FollowResponseDto;
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
	private String email;
	private int alcohol;
	private String socialProvider;
	private List<UserHistoryDto> metUser;
	 private List<FollowResponseDto>followingUser;
	 private List<BlockListDto>blacklistedUser;

	public MyPageResponseDto(User myPage, int alcohol, String socialProvider, List<UserHistoryDto> metUser, List<FollowResponseDto> followResponseDtos, List<BlockListDto> blockListDtos){
		this.userImageUrl= myPage.getUserImage();
		this.username= myPage.getUsername();
		this.email=myPage.getEmail();
		this.alcohol=alcohol;
		this.socialProvider=socialProvider;
		this.metUser = metUser;
		this.followingUser=followResponseDtos;
		this.blacklistedUser= blockListDtos;
	}

	public MyPageResponseDto(User myPage) {
		this.userImageUrl= myPage.getUserImage();
		this.username= myPage.getUsername();
	}


}
