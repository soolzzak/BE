package com.example.zzan.room.entity;

import com.example.zzan.room.dto.RoomRequestDto;
import com.example.zzan.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity(name = "TB_ROOM")
@Getter
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_ID")
    private Long id;

    private String title;

    private String username;

    private String category;

//    private String runningTime;

//    private String genderSetting;

//    private Boolean isPrivate;

//    private String roomPassword;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public Room(RoomRequestDto roomRequestDto){
        this.title = roomRequestDto.getTitle();
    }

    public Room(RoomRequestDto roomRequestDto, User user) {
        this.title = roomRequestDto.getTitle();
        this.username = user.getUsername();
        this.category = roomRequestDto.getCategory();
    }

    public void update(RoomRequestDto roomRequestDto) {
        this.title = roomRequestDto.getTitle();
        this.category = roomRequestDto.getCategory();
    }

//    public Room(RoomRequestDto roomRequestDto, User user) {
//        this
//    }

//    public void update (StreamRequestDto streamRequestDto){
//
//    }

}