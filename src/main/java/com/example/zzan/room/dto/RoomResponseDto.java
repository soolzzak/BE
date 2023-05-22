package com.example.zzan.room.dto;

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
    private Long userId;
    private String title;
    private String username;
    private String category;
//    private String runningTime;
    private String genderSetting;
    private Boolean isPrivate;
    private String roomPassword;
    private LocalDateTime createdAt;

    public RoomResponseDto(Room room){
        this.roomId = room.getId();
        this.userId = room.getUser().getId();
        this.title = room.getTitle();
        this.username = room.getUser().getUsername();
        this.category = room.getCategory();
        this.genderSetting = room.getGenderSetting();
        this.isPrivate = room.getIsPrivate();
        this.roomPassword = room.getRoomPassword();
        this.createdAt = room.getCreatedAt();
    }
}