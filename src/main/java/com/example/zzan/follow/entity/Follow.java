package com.example.zzan.follow.entity;

import com.example.zzan.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Follow {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name ="FOLLOWING_ID",nullable = false)
	private User followingUser;

	@ManyToOne
	@JoinColumn(name = "FOLLOWER_ID", nullable = false)
	private User followerUser;


	public Follow(User followingUser, User followerUser){
		this.followingUser = followingUser;
		this.followerUser = followerUser;
	}
	public User getFollowerUser() {
		return followerUser;
	}
}