package com.example.zzan.webRtc.dto;

import com.example.zzan.room.dto.RoomResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class UserListMap {
    private static UserListMap userListMap = new UserListMap();
    private Map<Long, RoomResponseDto> userMap = new LinkedHashMap<>();

    private UserListMap() {
    }

    public static UserListMap getInstance() {
        return userListMap;
    }
}