package com.example.zzan.blocklist.entity;

import com.example.zzan.global.Timestamped;
import com.example.zzan.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class BlockList extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name ="BLACKLISTEDUSER_ID",nullable = false)
	private User blockListedUser;

	@ManyToOne
	@JoinColumn(name = "BLACKLISTINGUSER_ID", nullable = false)
	private User blockListingUser;

	public BlockList(User blockListedUser, User blockListingUser){

		this.blockListedUser = blockListedUser;
		this.blockListingUser = blockListingUser;
	}
}
