package com.example.zzan.room.entity;

import com.example.zzan.global.Timestamped;
import com.example.zzan.room.dto.RoomRequestDto;
import com.example.zzan.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "TB_ROOM")
@Getter
@Setter
@NoArgsConstructor
public class Room extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_ID")
    private Long id;

    @NotNull(message = "방제목을 입력해주세요.")
    private String title;

    private String username;

    private String userImage;

    private String roomImage;

    @Column(name ="CATEGORY",columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci")
    @NotNull(message = "카테고리를 설정해주세요.")
    private Category category;

    private GenderSetting genderSetting;

    private Boolean isPrivate;

    private String roomPassword;

    @ManyToOne
    @JoinColumn(name = "HOST_ID")
    @JsonBackReference
    private User hostUser;

    public Room(RoomRequestDto roomRequestDto) {
        this.title = roomRequestDto.getTitle();
    }

    public Room(RoomRequestDto roomRequestDto, User user) {
        this.title = roomRequestDto.getTitle();
        this.roomImage = roomRequestDto.getRoomImage();
        this.userImage = user.getUserImage();
        this.hostUser = user;
        this.username = user.getUsername();
        this.category = roomRequestDto.getCategory();
        this.genderSetting = roomRequestDto.getGenderSetting();
        this.isPrivate = roomRequestDto.getIsPrivate();
        this.roomPassword = roomRequestDto.getRoomPassword();
    }

    public void update(RoomRequestDto roomRequestDto) {
        this.title = roomRequestDto.getTitle();
        this.roomImage = roomRequestDto.getRoomImage();
        this.userImage = hostUser.getUserImage();
        this.category = roomRequestDto.getCategory();
        this.genderSetting = roomRequestDto.getGenderSetting();
        this.isPrivate = roomRequestDto.getIsPrivate();
        this.roomPassword = roomRequestDto.getRoomPassword();
    }
}