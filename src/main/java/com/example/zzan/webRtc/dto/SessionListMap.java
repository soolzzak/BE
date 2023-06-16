package com.example.zzan.webRtc.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class SessionListMap {

    private static SessionListMap sessionListMap = new SessionListMap();
    private Map<WebSocketSession, Long> sessionMapToUserId = new LinkedHashMap<>();
    private Map<WebSocketSession, Long> sessionMapToRoom = new LinkedHashMap<>();
    private SessionListMap(){}

    public static SessionListMap getInstance(){
        return sessionListMap;
    }
}
