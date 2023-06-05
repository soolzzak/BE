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

    // roomID to room Mapping
    private final Map<Long, RoomResponseDto> rooms = UserListMap.getInstance().getUserMap();

    // message types, used in signalling:
    // SDP Offer message
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

    // 소켓 메시지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        // a message has been received
        //session=메세지가 들어오면 들어오는 타입에 대해서 종류별로 응답
        try {
            // 웹 소켓으로부터 전달받은 메시지
            // 소켓쪽에서는 socket.send 로 메시지를 발송한다 => 참고로 JSON 형식으로 변환해서 전달해온다
            WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class);
            logger.info("[ws] Message of {} type from {} received", message.getType(), message.getFrom());
            // 유저 uuid 와 roomID 를 저장
            Long userId = message.getFrom(); // 유저 uuid
            Long roomId = message.getData(); // roomId

            logger.info("Message {}", message.toString());

            RoomResponseDto room;

            // 메시지 타입에 따라서 서버에서 하는 역할이 달라진다
            switch (message.getType()) {

                // 클라이언트에게서 받은 메시지 타입에 따른 signal 프로세스
                case MSG_TYPE_OFFER:
                case MSG_TYPE_ANSWER:
                case MSG_TYPE_ICE:
                    Object candidate = message.getCandidate();
                    Object sdp = message.getSdp();

                    logger.info("[ws] Signal: {}",
                            candidate != null
                                    ? candidate.toString().substring(0, 64)
                                    : sdp.toString().substring(0, 64));

                    /* 여기도 마찬가지 */
                    RoomResponseDto roomDto = rooms.get(roomId);
                    logger.info("getRoom까지 완료!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");


                    if (roomDto != null) {
                        Map<Long, WebSocketSession> clients = rtcChatService.getUser(roomDto);
                        logger.info("roomDto에서 getUser 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                        for(Map.Entry<Long, WebSocketSession> client : clients.entrySet())  {
                            logger.info("상대방 가져오기 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                            // send messages to all clients except current user
                            if (!client.getKey().equals(userId)) {
                                logger.info("메세지 보낼 상대 찾기 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                                // select the same type to resend signal
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

                // identify user and their opponent
                case MSG_TYPE_JOIN:
                    // message.data contains connected room id
                    logger.info("[ws] {} has joined Room: #{}", userId, message.getData());


                    room = UserListMap.getInstance().getUserMap().get(roomId);

                    // // room 안에 있는 userList 에 유저 추가
                     rtcChatService.addUser(room, userId, session);

                    // // 채팅방 입장 후 유저 카운트+1
                    // chatServiceMain.plusUserCnt(roomId);

                    rooms.put(roomId, room);
                    break;

                case MSG_TYPE_LEAVE:
                    // message data contains connected room id
                    logger.info("[ws] {} is going to leave Room: #{}", userId, message.getData());

                    // roomID 기준 채팅방 찾아오기
                    room = rooms.get(message.getData());

                    // // room clients list 에서 해당 유저 삭제
                    // // 1. room 에서 client List 를 받아와서 keySet 을 이용해서 key 값만 가져온 후 stream 을 사용해서 반복문 실행
                    // Optional<String> client = rtcChatService.getClients(room).keySet().stream()
                    //         // 2. 이때 filter - 일종의 if문 -을 사용하는데 entry 에서 key 값만 가져와서 userUUID 와 비교한다
                    //         .filter(clientListKeys -> StringUtils.equals(clientListKeys, userUUID))
                    //         // 3. 하여튼 동일한 것만 가져온다
                    //         .findAny();
                    //
                    // // 만약 client 의 값이 존재한다면 - Optional 임으로 isPresent 사용 , null  아니라면 - removeClientByName 을 실행
                    // client.ifPresent(userID -> rtcChatService.removeClientByName(room, userID));

                    // // 채팅방에서 떠날 시 유저 카운트 -1
                    // chatServiceMain.minusUserCnt(roomId);

                    // logger.debug("삭제 완료 [{}] ",client);
                    break;

                // something should be wrong with the received message, since it's type is unrecognizable
                default:
                    logger.info("[ws] Type of the received message {} is undefined!", message.getType());
                    // handle this if needed
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
