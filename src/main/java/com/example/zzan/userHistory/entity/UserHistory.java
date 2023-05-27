package com.example.zzan.userHistory.entity;

import com.example.zzan.global.Timestamped;
import com.example.zzan.room.entity.Room;
import com.example.zzan.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserHistory extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_REPORT_ID")
	Long id;

	@ManyToOne
	@JoinColumn(name ="HOST_ID",nullable = false)
	private User hostUser;

	@ManyToOne
	@JoinColumn(name = "GUEST_ID", nullable = false)
	private User guestUser;

	@ManyToOne
	@JoinColumn(name = "ROOMHISTORY_ID", nullable = false)
	private Room room;


	public void setHostUser(User hostUser) {
		this.hostUser = hostUser;
	}

	public void setGuestUser(User user) {
		this.guestUser = user;
	}

	public void setRoom(Room room){this.room=room;}


}
