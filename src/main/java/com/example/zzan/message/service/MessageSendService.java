package com.example.zzan.message.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.message.dto.MessageSendRequestDto;
import com.example.zzan.message.entity.Messages;
import com.example.zzan.message.repository.MessageRepository;
import com.example.zzan.sse.service.SseService;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.zzan.global.exception.ExceptionEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSendService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SseService sseService;

    public ResponseDto<?> sendMessage(User user, MessageSendRequestDto messageSendRequestDto) {

        User messageReceiver = userRepository.findByUsername(messageSendRequestDto.getReceiverUsername()).orElseThrow(
                () -> new ApiException(USER_NOT_FOUND)
        );
        String messageContent = messageSendRequestDto.getContent();

        if (messageReceiver.getUsername().equals(user.getUsername())) {
            throw new ApiException(NOT_ALLOWED_SELF_MESSAGE);
        }

        if (messageContent.isEmpty()) {
            throw new ApiException(NO_MESSAGE);
        }

        saveMessage(user, messageReceiver, messageContent);
        sseService.sendSseMessage(messageReceiver.getUsername(), user.getUsername());

        return ResponseDto.setSuccess("Message successfully sent.");
    }

    public void saveMessage(User sendUser, User receiveUser, String content) {
        Messages messages = new Messages(sendUser, receiveUser, content, false);
        messageRepository.save(messages);
    }
}


