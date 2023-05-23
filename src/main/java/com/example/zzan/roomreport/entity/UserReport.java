package com.example.zzan.roomreport.entity;

import com.example.zzan.global.Timestamped;
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

@Entity
@Getter
@NoArgsConstructor
public class UserReport extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UserReport_id")
	Long id;

	@ManyToOne
	@JoinColumn(name ="host_user_id",nullable = false)
	private User hostUser;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User enterUser;



	public void setHostUser(User hostUser) {
		this.hostUser = hostUser;
	}

	public void setEnterUser(User user) {
		this.enterUser = user;
	}


}