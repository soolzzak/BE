package com.example.zzan.webRtc.rtc;

import com.example.zzan.global.exception.ApiException;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.room.entity.Room;
import com.example.zzan.room.repository.RoomRepository;
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

import static com.example.zzan.global.exception.ExceptionEnum.ROOM_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class SignalHandler extends TextWebSocketHandler {

    private final RtcChatService rtcChatService;
    private final RoomRepository roomRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<Long, RoomResponseDto> rooms = UserListMap.getInstance().getUserMap();


    private static final String MSG_TYPE_OFFER = "offer";
    // SDP Answer message
    private static final String MSG_TYPE_ANSWER = "answer";
    // New ICE Candidate message
    private static final String MSG_TYPE_ICE = "ice";
    // join room data message
    private static final String MSG_TYPE_JOIN = "join";
    // leave room data message
    private static final String MSG_TYPE_LEAVE = "leave";

    // 연결 끊어졌을 때 이벤트처리
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
            Long userId = message.getFrom();
            Long roomId = message.getData();

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

                    if (roomDto != null) {
                        Map<Long, WebSocketSession> clients = rtcChatService.getUser(roomDto);

                        for(Map.Entry<Long, WebSocketSession> client : clients.entrySet())  {

                            if (!client.getKey().equals(userId)) {

                                sendMessage(client.getValue(),
                                    new WebSocketMessage(
                                        userId,
                                        message.getType(),
                                        roomId,
                                        candidate,
                                        sdp));
                            }
                        }
                    }
                    break;

                case MSG_TYPE_JOIN:
                    logger.info("[ws] {} has joined Room: #{}", userId, message.getData());

                    room = UserListMap.getInstance().getUserMap().get(roomId);

                    rtcChatService.addUser(room, userId, session);
                    rtcChatService.addUser(room, userId, session);

                    rooms.put(roomId, room);
                    break;



                case MSG_TYPE_LEAVE:
                    logger.info("[ws] {} is going to leave Room: #{}", userId, message.getData());

                    room = rooms.get(message.getData());
                    Room realroom = roomRepository.findById(room.getRoomId()). orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));

                    if(room.getHostId().equals(userId)){

                        realroom.roomDelete(true);
                        roomRepository.save(realroom);
                        break;
                    }else if(!room.getHostId().equals(userId)){

                        realroom.setRoomCapacity(room.getRoomCapacity() - 1);
                        roomRepository.save(realroom);
                        break;
                    }



                    break;

                default:
                    logger.info("[ws] Type of the received message {} is undefined!", message.getType());
            }

        } catch (IOException e) {
            logger.info("An error occured: {}", e.getMessage());
        }
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.info("An error occured: {}", e.getMessage());
        }
    }
}