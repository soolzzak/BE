package com.example.zzan.room.dto;

import com.example.zzan.room.entity.Category;
import com.example.zzan.room.entity.GenderSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomRequestDto {

    private String title;
    private String roomImage;
    private Category category;
    private GenderSetting genderSetting;
    private Boolean isPrivate;
    private String roomPassword;
}