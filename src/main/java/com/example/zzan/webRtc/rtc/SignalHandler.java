package com.example.zzan.webRtc.rtc;

import static com.example.zzan.global.exception.ExceptionEnum.*;

import com.example.zzan.global.exception.ApiException;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.room.entity.Room;
import com.example.zzan.room.repository.RoomRepository;
import com.example.zzan.room.service.RoomService;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import com.example.zzan.webRtc.dto.SessionListMap;
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
    private final RoomRepository roomRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RoomService roomService;

    private final UserRepository userRepository;

    private Map<Long, RoomResponseDto> rooms = UserListMap.getInstance().getUserMap();

    private Map<WebSocketSession, Long> sessions = SessionListMap.getInstance().getSessionMapToUserId();
    private Map<WebSocketSession, Long> sessions2 = SessionListMap.getInstance().getSessionMapToRoom();

    private static final String MSG_TYPE_INFO = "info";
    private static final String MSG_TYPE_OFFER = "offer";
    private static final String MSG_TYPE_ANSWER = "answer";
    private static final String MSG_TYPE_ICE = "ice";
    private static final String MSG_TYPE_JOIN = "join";
    private static final String MSG_TYPE_LEAVE = "leave";
    private static final String MSG_TYPE_TOAST = "toast";
    private static final String MSG_TYPE_PING = "ping";
    private static final String MSG_TYPE_KICK = "kick";
    private static final String MSG_TYPE_START = "startShare";
    private static final String MSG_TYPE_STOP = "stopShare";

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long sessionUserId = sessions.get(session);
        Long sessionRoomId = sessions2.get(session);

        if (rooms.get(sessionRoomId) != null) {
            RoomResponseDto roomDto = rooms.get(sessionRoomId);

            Room realroom = roomRepository.findById(roomDto.getRoomId())
                    .orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));
            Long hostId = roomDto.getHostId();

            if (hostId != null) {
                if (roomDto.getHostId().equals(sessionUserId)) {
                    realroom.roomDelete(true);
                    roomRepository.save(realroom);
                } else if (!roomDto.getHostId().equals(sessionUserId)) {
                    realroom.setRoomCapacity(roomDto.getRoomCapacity() - 1);
                    roomRepository.save(realroom);
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long sessionUserId = sessions.get(session);
        


        sendMessage(session, new WebSocketMessage(sessionUserId,MSG_TYPE_INFO, null, null, null));
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

                        for (Map.Entry<Long, WebSocketSession> client : clients.entrySet()) {
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
                    Room existingRoom = roomRepository.findById(room.getRoomId())
                        .orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));

                    if (existingRoom.getRoomCapacity() <= 2) {
                        rtcChatService.addUser(room, userId, session);
                        existingRoom.setRoomCapacity(existingRoom.getRoomCapacity() + 1);

                        rooms.put(roomId, room);
                        roomRepository.saveAndFlush(existingRoom);
                        Map<Long, WebSocketSession> joinClients = rtcChatService.getUser(room);
                        for (Map.Entry<Long, WebSocketSession> client : joinClients.entrySet()) {
                            if (client.getKey().equals(userId)) {
                                sendMessage(client.getValue(),
                                    new WebSocketMessage(
                                        userId,
                                        message.getType(),
                                        roomId,
                                        null,
                                        null));
                            }
                        }

                    }else {
                        throw new ApiException(ROOM_ALREADY_FULL);
                    }

                    break;

                case MSG_TYPE_LEAVE:
                    logger.info("[ws] {} is going to leave Room: #{}", userId, message.getData());

                    room = rooms.get(message.getData());
                    Room realroom = roomRepository.findById(room.getRoomId()).orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));

                    if (room.getHostId().equals(userId)) {
                        realroom.roomDelete(true);
                        roomRepository.save(realroom);
                        break;
                    } else if (!room.getHostId().equals(userId)) {
                        realroom.setRoomCapacity(room.getRoomCapacity() - 1);
                        roomRepository.save(realroom);
                        break;
                    }
                    break;

                case MSG_TYPE_TOAST, MSG_TYPE_START, MSG_TYPE_STOP:
                    room = rooms.get(message.getData());

                    Map<Long, WebSocketSession> clients = rtcChatService.getUser(room);
                    for (Map.Entry<Long, WebSocketSession> client : clients.entrySet()) {
                        if (!client.getKey().equals(userId)) {
                            sendMessage(client.getValue(),
                                    new WebSocketMessage(
                                            userId,
                                            message.getType(),
                                            roomId,
                                            null,
                                            null));
                        }
                    }
                    break;

                case MSG_TYPE_PING :
                    room = rooms.get(message.getData());
                    Long hostId = room.getHostId();
                    Map<Long, WebSocketSession> pingUser = rtcChatService.getUser(room);
                    WebSocketSession hostSession = pingUser.get(hostId);

                    sendMessage(hostSession,
                            new WebSocketMessage(
                                    userId,
                                    message.getType(),
                                    roomId,
                                    null,
                                    null));
                    break;

                case MSG_TYPE_KICK :
                    room = rooms.get(message.getData());
                    Map<Long, WebSocketSession> clientsInRoom = rtcChatService.getUser(room);
                    Long hostIdInRoom = room.getHostId();

                    if(userId.equals(hostIdInRoom)) {
                        for (Map.Entry<Long, WebSocketSession> client : clientsInRoom.entrySet()) {
                            if (client.getKey().equals(hostIdInRoom)) {
                                for (Map.Entry<Long, WebSocketSession> Guestclient : clientsInRoom.entrySet()) {
                                    if (!Guestclient.getKey().equals(hostIdInRoom)) {
                                        Long guestId = Guestclient.getKey();
                                        User guestUser = userRepository.findById(guestId).get();
                                        sendMessage(Guestclient.getValue(),
                                            new WebSocketMessage(
                                                userId,
                                                message.getType(),
                                                roomId,
                                                null,
                                                null));
                                        roomService.leaveRoom(roomId, guestUser);

                                        WebSocketSession guestSession = Guestclient.getValue();
                                        if (guestSession.isOpen()) { // 웹소켓 세션이 열려있는지 확인
                                            try {
                                                guestSession.close();
                                            } catch (IOException e) {
                                                // 로그에 오류를 기록하거나 적절한 조치를 취하십시오.
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }else {
                        throw new ApiException(ONLY_HOST_CAN_KICK);
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