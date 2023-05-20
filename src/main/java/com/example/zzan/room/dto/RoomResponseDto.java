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
    private String title;
    private String username;
    private String category;
    private LocalDateTime createdAt;

    public RoomResponseDto(Room room){
        this.roomId = room.getId();
        this.title = room.getTitle();
        this.username = room.getUsername();
        this.category = room.getCategory();
        this.createdAt = room.getCreatedAt();

    }




}