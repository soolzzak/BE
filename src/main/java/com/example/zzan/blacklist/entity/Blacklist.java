package com.example.zzan.blacklist.entity;

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

@Getter
@Entity
@NoArgsConstructor
public class Blacklist {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name ="BLACKLISTEDUSER_ID",nullable = false)
	private User blackListedUser;

	@ManyToOne
	@JoinColumn(name = "BLACKLISTINGUSER_ID", nullable = false)
	private User blackListingUser;

	public Blacklist(User blackListedUser,User blackListingUser){

		this.blackListedUser=blackListedUser;
		this.blackListingUser=blackListingUser;

	}


}
