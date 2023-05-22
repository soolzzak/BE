package com.example.zzan.mypage.dto;

import com.example.zzan.mypage.entity.MyPage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageResponseDto {

	private String imgUrl;
	private String nickname;

	public MyPageResponseDto(MyPage myPage){

		this.imgUrl= myPage.getImg();
		this.nickname= myPage.getNickname();
	}

}
