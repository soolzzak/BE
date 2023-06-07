package com.example.zzan.webRtc.dto;



import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.socket.WebSocketSession;

import lombok.Getter;
import lombok.Setter;

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
