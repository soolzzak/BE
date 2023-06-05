package com.example.zzan.room.dto;

import com.example.zzan.room.entity.Category;
import com.example.zzan.room.entity.GenderSetting;
import com.example.zzan.room.entity.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    private Map<Long, WebSocketSession> userList;

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
        this.userList = new HashMap<>();
    }

//    public RoomResponseDto(Long userId, WebSocketSession session){
//        this.userList = new HashMap<>();
//        this.userList.put(userId, session);
//    }
//



}