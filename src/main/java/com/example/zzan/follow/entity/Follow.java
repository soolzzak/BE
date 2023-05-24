package com.example.zzan.follow.entity;

import com.example.zzan.follow.dto.FollowRuquestDto;
import com.example.zzan.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
public class Follow {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;


	@Column
	private String followingUserEmail;

	@Column
	private String UserEmail;

	public Follow(FollowRuquestDto followRuquestDto, User user){

		this.followingUserEmail=followRuquestDto.getFollowingUserEmail();
		this.UserEmail=user.getEmail();

	}

}
