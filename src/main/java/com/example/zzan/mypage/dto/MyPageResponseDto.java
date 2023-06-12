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

	private String userImage;
	private String username;
	private String email;
	private int alcohol;
	private List<UserHistoryDto> metUser;
	private List<FollowResponseDto> followingUser;
	private List<BlockListDto> blockListedUser;

	public MyPageResponseDto(User myPage, int alcohol, List<UserHistoryDto> metUser, List<FollowResponseDto> followResponseDtos, List<BlockListDto> blockListDtos) {
		this.userImage = myPage.getUserImage();
		this.username = myPage.getUsername();
		this.email = myPage.getEmail();
		this.alcohol = alcohol;
		this.metUser = metUser;
		this.followingUser = followResponseDtos;
		this.blockListedUser = blockListDtos;
	}
}
