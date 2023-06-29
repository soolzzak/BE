package com.example.zzan.webRtc.rtc;

import static com.example.zzan.global.exception.ExceptionEnum.*;

import com.example.zzan.game.service.GameService;
import com.example.zzan.game.dto.GameResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.icebreaker.dto.IceBreakerDto;
import com.example.zzan.icebreaker.service.IceBreakerService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignalHandler extends TextWebSocketHandler {

    private final RtcChatService rtcChatService;
    private final RoomRepository roomRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RoomService roomService;
    private final GameService gameService;
    private final IceBreakerService iceBreakerService;
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
    private static final String MSG_TYPE_GAMEINFO = "gameInfo";
    private static final String MSG_TYPE_STARTGAME = "startGame";
    private static final String MSG_TYPE_PAUSEGAME = "pauseGame";
    private static final String MSG_TYPE_STOPGAME = "stopGame";
    private static final String MSG_TYPE_YOUTUBE = "youtube";
    private static final String MSG_TYPE_STARTYOUTUBE = "startYoutube";
    private static final String MSG_TYPE_PAUSEYOUTUBE = "pauseYoutube";
    private static final String MSG_TYPE_STOPYOUTUBE = "stopYoutube";
    private static final String MSG_TYPE_ICEBREAKER = "iceBreaker";
    private static final String MSG_TYPE_SENDPICTURE = "sendPicture";
    private static final String MSG_TYPE_CONFIRMPICTURE = "confirmPicture";
    private static final String MSG_TYPE_GUESTDISCONNECT= "guestDisconnect";
    private static final String MSG_TYPE_HOSTDISCONNECT= "hostDisconnect";
    private static final String MSG_TYPE_DENYPICTURE = "denyPicture";

    @Override
    @Transactional
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        Long sessionUserId = sessions.get(session);
        Long sessionRoomId = sessions2.get(session);


        RoomResponseDto roomDto = rooms.get(sessionRoomId);


        if (rooms.get(sessionRoomId) != null) {
            Room realroom = roomRepository.findById(roomDto.getRoomId())
                .orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));
            Long hostId = roomDto.getHostId();

            if (hostId != null) {
                if (roomDto.getHostId().equals(sessionUserId)) {
                    realroom.roomDelete(true);
                    roomRepository.saveAndFlush(realroom);
                } else if (!roomDto.getHostId().equals(sessionUserId)) {
                    realroom.setRoomCapacity(realroom.getRoomCapacity() - 1);
                    roomRepository.saveAndFlush(realroom);
                }
            }

            Map<Long, WebSocketSession> clients = rtcChatService.getUser(roomDto);
            for (Map.Entry<Long, WebSocketSession> client : clients.entrySet()) {
                WebSocketSession clientSession = client.getValue();

                if (!client.getKey().equals(sessionUserId) && client.getKey().equals(hostId)&& clientSession.isOpen()) {
                    sendMessage(client.getValue(),
                        new WebSocketMessage(
                            sessionUserId,
                            MSG_TYPE_GUESTDISCONNECT,
                            null,
                            0,
                            null,
                            "The guest has left the room.",
                            null,
                            null));
                } else if (!client.getKey().equals(sessionUserId) && !client.getKey().equals(hostId)&& clientSession.isOpen()) {
                    sendMessage(client.getValue(),
                        new WebSocketMessage(
                            sessionUserId,
                            MSG_TYPE_HOSTDISCONNECT,
                            null,
                            0,
                            null,
                            "The host has left the room.",
                            null,
                            null));
                }
            }

        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        Long sessionUserId = sessions.get(session);


        sendMessage(session, new WebSocketMessage(sessionUserId, MSG_TYPE_INFO, null, 0, null, null, null, null));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        try {
            WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class);
            logger.info("[ws] Message of {} type from {} received", message.getType(), message.getFrom());
            Long userId = message.getFrom();
            Long roomId = message.getData();
            Double time = message.getTime();
            String youtubeUrl = message.getYoutubeUrl();

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
                            WebSocketSession clientSession = client.getValue();
                            if (!client.getKey().equals(userId)&& clientSession.isOpen()) {
                                sendMessage(client.getValue(),
                                    new WebSocketMessage(
                                        userId,
                                        message.getType(),
                                        roomId,
                                        0,
                                        null,
                                        null,
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

                    if (existingRoom.getRoomCapacity() < 3) {
                        rtcChatService.addUser(room, userId, session);

                        rooms.put(roomId, room);
                        Map<Long, WebSocketSession> joinClients = rtcChatService.getUser(room);
                        for (Map.Entry<Long, WebSocketSession> client : joinClients.entrySet()) {
                            WebSocketSession clientSession = client.getValue();
                            if (client.getKey().equals(userId)&& clientSession.isOpen()) {
                                sendMessage(client.getValue(),
                                    new WebSocketMessage(
                                        userId,
                                        message.getType(),
                                        roomId,
                                        0,
                                        null,
                                        null,
                                        null,
                                        null));
                            }
                        }

                    }
                    else {
                        Map<Long, WebSocketSession> joinClients = rtcChatService.getUser(room);
                        session.close();
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

                case MSG_TYPE_TOAST, MSG_TYPE_START, MSG_TYPE_STOP, MSG_TYPE_PAUSEYOUTUBE, MSG_TYPE_STOPYOUTUBE, MSG_TYPE_SENDPICTURE, MSG_TYPE_GAMEINFO,MSG_TYPE_DENYPICTURE:
                    room = rooms.get(message.getData());

                    Map<Long, WebSocketSession> clients = rtcChatService.getUser(room);
                    for (Map.Entry<Long, WebSocketSession> client : clients.entrySet()) {
                        if (!client.getKey().equals(userId)&& client.getValue().isOpen()) {
                            sendMessage(client.getValue(),
                                new WebSocketMessage(
                                    userId,
                                    message.getType(),
                                    roomId,
                                    0,
                                    null,
                                    null,
                                    null,
                                    null));
                        }
                    }
                    break;

                case MSG_TYPE_PING:
                    room = rooms.get(message.getData());
                    Long hostId = room.getHostId();
                    Map<Long, WebSocketSession> pingUser = rtcChatService.getUser(room);
                    WebSocketSession hostSession = pingUser.get(hostId);

                    sendMessage(hostSession,
                        new WebSocketMessage(
                            userId,
                            message.getType(),
                            roomId,
                            0,
                            null,
                            null,
                            null,
                            null));
                    break;

                case MSG_TYPE_KICK:
                    room = rooms.get(message.getData());
                    Map<Long, WebSocketSession> clientsInRoom = rtcChatService.getUser(room);
                    Long hostIdInRoom = room.getHostId();

                    if (userId.equals(hostIdInRoom)) {
                        for (Map.Entry<Long, WebSocketSession> client : clientsInRoom.entrySet()) {
                            if (client.getKey().equals(hostIdInRoom)) {
                                for (Map.Entry<Long, WebSocketSession> Guestclient : clientsInRoom.entrySet()) {
                                    if (!Guestclient.getKey().equals(hostIdInRoom)) {

                                        Long guestId = Guestclient.getKey();
                                        User guestUser = userRepository.findById(guestId).get();

                                        roomService.leaveRoom(roomId, guestUser);
                                        WebSocketSession guestSession = Guestclient.getValue();
                                        if (guestSession.isOpen()) {
                                            try {
                                                sendMessage(Guestclient.getValue(),
                                                    new WebSocketMessage(
                                                        userId,
                                                        message.getType(),
                                                        roomId,
                                                        0,
                                                        null,
                                                        "강퇴 되었습니다",
                                                        null,
                                                        null));
                                                guestSession.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        throw new ApiException(ONLY_HOST_CAN_KICK);
                    }
                    break;

                case MSG_TYPE_STARTGAME:
                    room = rooms.get(message.getData());
                    Map<Long, WebSocketSession> startGamePlayers = rtcChatService.getUser(room);
                    for (Map.Entry<Long, WebSocketSession> client : startGamePlayers.entrySet()) {
                        gameSendMessage(client.getValue(),
                            new GameResponseDto(
                                userId,
                                message.getType(),
                                "게임 시작!",
                                null,
                                null));
                    }
                    gameService.startGame(startGamePlayers);
                    break;

                case MSG_TYPE_PAUSEGAME:
                    room = rooms.get(message.getData());
                    Map<Long, WebSocketSession> pauseGamePlayers = rtcChatService.getUser(room);
                    gameService.pauseGame(pauseGamePlayers);
                    break;

                case MSG_TYPE_STOPGAME:
                    room = rooms.get(message.getData());
                    Map<Long, WebSocketSession> stopGamePlayers = rtcChatService.getUser(room);
                    gameService.finishGame(stopGamePlayers);
                    break;

                case MSG_TYPE_STARTYOUTUBE:

                    room = rooms.get(message.getData());

                    Map<Long, WebSocketSession> youtubeViewers = rtcChatService.getUser(room);
                    for (Map.Entry<Long, WebSocketSession> client : youtubeViewers.entrySet()) {
                        if (!client.getKey().equals(userId)) {
                            sendMessage(client.getValue(),
                                new WebSocketMessage(
                                    userId,
                                    message.getType(),
                                    roomId,
                                    time,
                                    null,
                                    null,
                                    null,
                                    null));
                        }
                    }
                    break;

                case MSG_TYPE_YOUTUBE:
                    room = rooms.get(message.getData());

                    Map<Long, WebSocketSession> usersWatchingYoutube = rtcChatService.getUser(room);
                    for (Map.Entry<Long, WebSocketSession> client : usersWatchingYoutube.entrySet()) {
                        if (!client.getKey().equals(userId)) {
                            sendMessage(client.getValue(),
                                new WebSocketMessage(
                                    userId,
                                    message.getType(),
                                    roomId,
                                    0,
                                    youtubeUrl,
                                    null,
                                    null,
                                    null));
                        }
                    }
                    break;

                case MSG_TYPE_ICEBREAKER:
                    room = rooms.get(message.getData());
                    Map<Long, WebSocketSession> iceBreaker = rtcChatService.getUser(room);
                    String question = iceBreakerService.getRandomQuestion();
                    for (Map.Entry<Long, WebSocketSession> client : iceBreaker.entrySet()) {
                        iceBreakSendMessage(client.getValue(),
                            new IceBreakerDto(
                                userId,
                                message.getType(),
                                question,
                                null,
                                null));
                    }
                    //                    iceBreakerService.displayQuestion(iceBreaker);
                    break;


                case MSG_TYPE_CONFIRMPICTURE:
                    room = rooms.get(message.getData());

                    Map<Long, WebSocketSession> pictureTakingUsers  = rtcChatService.getUser(room);
                    for (Map.Entry<Long, WebSocketSession> client : pictureTakingUsers .entrySet()) {

                        sendMessage(client.getValue(),
                            new WebSocketMessage(
                                userId,
                                message.getType(),
                                roomId,
                                0,
                                null,
                                null,
                                null,
                                null));

                    }
                    break;




                default:
                    logger.info("[ws] Type of the received message {} is undefined!", message.getType());
            }
        } catch (IOException e) {
            logger.info("An error occured: {}", e.getMessage());
        }
    }

    public void sendMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.info("An error occured: {}", e.getMessage());
        }
    }

    public void gameSendMessage(WebSocketSession session, GameResponseDto gameResponseDto) {
        try {
            String json = objectMapper.writeValueAsString(gameResponseDto);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.info("An error occured: {}", e.getMessage());
        }
    }

    public void iceBreakSendMessage(WebSocketSession session, IceBreakerDto iceBreakerDto) {
        try {
            String json = objectMapper.writeValueAsString(iceBreakerDto);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.info("An error occured: {}", e.getMessage());
        }
    }
}