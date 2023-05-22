package com.example.zzan.mypage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class MyPage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MyPage_ID")
	private Long id;
	private String nickname;
	private String img;

	public MyPage(String nickname, String img) {
		this.nickname=nickname;
		this.img = img;
	}

	public void updateImg(String img){
		this.img = img;
	}

	public void updateNickname(String nickname){
		this.nickname = nickname;
	}
}