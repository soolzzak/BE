package com.example.zzan.room.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "ROOMHISTORY")
@Getter
@Setter
@NoArgsConstructor
public class RoomHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ROOMHISTORY_ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "ROOM_ID")
	@JsonBackReference
	private Room room;


	public RoomHistory(Room room){
		this.room = room;
	}

}
