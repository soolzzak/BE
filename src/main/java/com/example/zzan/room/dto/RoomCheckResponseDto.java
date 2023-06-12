package com.example.zzan.room.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomCheckResponseDto {
    private int roomCapacity;

    public RoomCheckResponseDto(int roomCapacity) {
        this.roomCapacity = roomCapacity;
    }
}