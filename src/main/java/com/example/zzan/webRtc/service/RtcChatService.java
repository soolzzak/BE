package com.example.zzan.webRtc.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.webRtc.dto.UserListMap;
import com.example.zzan.webRtc.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class RtcChatService {

    public Map<Long, WebSocketSession> getUser (RoomResponseDto roomResponseDto) {

        Optional<RoomResponseDto> roomDto = Optional.ofNullable(roomResponseDto);

        return roomDto.get().getUserList();
    }


    public ResponseDto addUser(RoomResponseDto roomResponseDto, Long userId, WebSocketSession session) {

        Map<Long, WebSocketSession> userList = roomResponseDto.getUserList();
        userList.put(userId, session);
        return ResponseDto.setSuccess("유저 리스트가 추가 되었습니다", userList);
    }

//    // userList 에서 클라이언트 삭제
//    public void removeClientByName(RoomResponseDto room, Long userId) {
//        room.getUserList().remove(userId);
//    }

    // 유저 카운터 return
    public boolean findUserCount(WebSocketMessage webSocketMessage){
        RoomResponseDto room = UserListMap.getInstance().getUserMap().get(webSocketMessage.getData());
        log.info("ROOM COUNT : [{} ::: {}]",room.toString(),room.getUserList().size());
        return room.getUserList().size() > 1;
    }
}