package com.example.zzan.room.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomRequestDto {

    private String title;
    private String roomImage;
    private String category;
//    private String runningTime;
    private String genderSetting;
    private Boolean isPrivate;
    private String roomPassword;
}