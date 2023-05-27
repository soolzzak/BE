package com.example.zzan.room.dto;

import com.example.zzan.room.entity.Category;
import com.example.zzan.room.entity.GenderSetting;
import com.example.zzan.room.entity.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RoomResponseDto {
    private Long roomId;
    private Long hostId;
    private String title;
    private String username;
    private Category category;
    private GenderSetting genderSetting;
    private Boolean isPrivate;
    private String roomPassword;
    private LocalDateTime createdAt;
    private int alcohol;
    private String userImageUrl;
    private String roomImageUrl;


    public RoomResponseDto(Room room){
        this.roomId = room.getId();
        this.hostId = room.getHostUser().getId();
        this.title = room.getTitle();
        this.username = room.getHostUser().getUsername();
        this.category = room.getCategory();
        this.genderSetting = room.getGenderSetting();
        this.isPrivate = room.getIsPrivate();
        this.roomPassword = room.getRoomPassword();
        this.createdAt = room.getCreatedAt();
        this.alcohol = room.getHostUser().getAlcohol();
        this.userImageUrl = room.getHostUser().getUserImage();
        this.roomImageUrl = room.getRoomImage();
    }
}