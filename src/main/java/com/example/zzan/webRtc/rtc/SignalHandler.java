package com.example.zzan.webRtc.rtc;

import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.webRtc.dto.UserListMap;
import com.example.zzan.webRtc.dto.WebSocketMessage;
import com.example.zzan.webRtc.service.RtcChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class SignalHandler extends TextWebSocketHandler {

    private final RtcChatService rtcChatService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Long, RoomResponseDto> rooms = UserListMap.getInstance().getUserMap();


    private static final String MSG_TYPE_OFFER = "offer";
    private static final String MSG_TYPE_ANSWER = "answer";
    private static final String MSG_TYPE_ICE = "ice";
    private static final String MSG_TYPE_JOIN = "join";
    private static final String MSG_TYPE_LEAVE = "leave";

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("[ws] Session has been closed with status [{} {}]", status, session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sendMessage(session, new WebSocketMessage(null, MSG_TYPE_JOIN, null, null, null));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {

        try {
            WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class);
            logger.info("[ws] Message of {} type from {} received", message.getType(), message.getFrom());
            Long userId = message.getFrom(); // 유저 uuid
            Long roomId = message.getData(); // roomId

            logger.info("Message {}", message.toString());

            RoomResponseDto room;

            switch (message.getType()) {

                case MSG_TYPE_OFFER:
                case MSG_TYPE_ANSWER:
                case MSG_TYPE_ICE:
                    Object candidate = message.getCandidate();
                    Object sdp = message.getSdp();

                    logger.info("[ws] Signal: {}",
                            candidate != null
                                    ? candidate.toString().substring(0, 64)
                                    : sdp.toString().substring(0, 64));

                    RoomResponseDto roomDto = rooms.get(roomId);
                    logger.info("getRoom까지 완료!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");


                    if (roomDto != null) {
                        Map<Long, WebSocketSession> clients = rtcChatService.getUser(roomDto);
                        logger.info("roomDto에서 getUser 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                        for(Map.Entry<Long, WebSocketSession> client : clients.entrySet())  {
                            logger.info("상대방 가져오기 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                            if (!client.getKey().equals(userId)) {
                                logger.info("메세지 보낼 상대 찾기 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                                sendMessage(client.getValue(),
                                        new WebSocketMessage(
                                                userId,
                                                message.getType(),
                                                roomId,
                                                candidate,
                                                sdp));
                                logger.info("상대방에게 메세지 보내기 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            }
                            logger.info("주고 받기 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        }
                    }
                    break;

                case MSG_TYPE_JOIN:
                    logger.info("[ws] {} has joined Room: #{}", userId, message.getData());

                    room = UserListMap.getInstance().getUserMap().get(roomId);

                     rtcChatService.addUser(room, userId, session);

                    rooms.put(roomId, room);
                    break;

                case MSG_TYPE_LEAVE:
                    logger.info("[ws] {} is going to leave Room: #{}", userId, message.getData());

                    room = rooms.get(message.getData());

                    break;

                default:
                    logger.info("[ws] Type of the received message {} is undefined!", message.getType());
            }

        } catch (IOException e) {
            logger.info("An error occurred: {}", e.getMessage());
        }
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.info("An error occurred: {}", e.getMessage());
        }
    }
}
