package com.example.zzan.message.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.message.dto.MessageCheckResponseDto;
import com.example.zzan.message.entity.Messages;
import com.example.zzan.message.repository.MessageRepository;
import com.example.zzan.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.zzan.global.exception.ExceptionEnum.INTERNAL_SERVER_ERROR;
import static com.example.zzan.global.exception.ExceptionEnum.MESSAGE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageCheckService {
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public ResponseDto<List<MessageCheckResponseDto>> checkReceivedMessage(User user) {
        List<MessageCheckResponseDto> messageList = new ArrayList<>();

        List<Messages> list = messageRepository.findAllByReceiveUserOrderByCreatedAtDesc(user);

        for (Messages messages : list) {
            MessageCheckResponseDto messageCheckResponseDto = new MessageCheckResponseDto(messages);
            messageList.add(messageCheckResponseDto);
        }

        if (messageList.isEmpty()) {
            return ResponseDto.setSuccess("There is no received message list.");
        } else {
            return ResponseDto.setSuccess("Successfully got received message list.", messageList);
        }
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<MessageCheckResponseDto>> checkSentMessage(User user) {
        List<MessageCheckResponseDto> messageList = new ArrayList<>();

        List<Messages> list = messageRepository.findAllBySendUserOrderByCreatedAtDesc(user);

        for (Messages messages : list) {
            MessageCheckResponseDto message = new MessageCheckResponseDto(messages);
            messageList.add(message);
        }

        if (messageList.isEmpty()) {
            return ResponseDto.setSuccess("There is no sent message.");
        } else {
            return ResponseDto.setSuccess("Successfully got sent message list.", messageList);
        }
    }

    @Transactional(readOnly = true)
    public ResponseDto<MessageCheckResponseDto> readMessage(User user, Long messageId) {
        Messages findMessage = messageRepository.findById(messageId).orElseThrow(
                () -> new ApiException(MESSAGE_NOT_FOUND)
        );

        if (user.getId().equals(findMessage.getReceiveUser().getId())) {
            findMessage.markRead();
            messageRepository.saveAndFlush(findMessage);
            MessageCheckResponseDto responseDto = new MessageCheckResponseDto(findMessage);
            return ResponseDto.setSuccess("Successfully read message.", responseDto);
        } else {
            throw new ApiException(INTERNAL_SERVER_ERROR);
        }
    }
}
