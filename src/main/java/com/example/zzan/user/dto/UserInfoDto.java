package com.example.zzan.user.dto;

import java.util.Date;

import com.example.zzan.room.entity.Room;
import com.example.zzan.user.entity.Gender;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.entity.UserRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserInfoDto {

	private Long id;
	private String kakaoId;
	private String email;
	private String password;
	private String username;
	private Date birthday;
	private Gender gender;
	private UserRole role;
	private String userImage;
	private int reportPoints = 0;
	private int alcohol;
	private boolean alcoholUp = false;
	private boolean alcoholDown = false;
	private Room room;
	private String roomTitle;
	private boolean deleteAccount = false;
	private String introduction;

	public UserInfoDto(User user) {
		this.id = user.getId();
		this.kakaoId = user.getKakaoId();
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.username = user.getUsername();
		this.birthday = user.getBirthday();
		this.gender = user.getGender();
		this.role = user.getRole();
		this.userImage = user.getUserImage();
		this.reportPoints = user.getReportPoints();
		this.alcohol = user.getAlcohol();
		this.alcoholUp = user.isAlcoholUp();
		this.alcoholDown = user.isAlcoholDown();
		this.room = user.getRoom();
		this.roomTitle = user.getRoomTitle();
		this.deleteAccount = user.isDeleteAccount();
		this.introduction = user.getIntroduction();
	}



}
