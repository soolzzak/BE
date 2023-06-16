package com.example.zzan.message.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.message.dto.MessageSendRequestDto;
import com.example.zzan.message.service.MessageCheckService;
import com.example.zzan.message.service.MessageSendService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MessageController", description = "메세지 파트")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageSendService messageSendService;
    private final MessageCheckService messageCheckService;

    @PostMapping("/send")
    public ResponseDto<?> sendMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody MessageSendRequestDto messageSendRequestDto) {
        return messageSendService.sendMessage(userDetails.getUser(), messageSendRequestDto);
    }

    @GetMapping("/received")
    public ResponseDto<?> receivedMessage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return messageCheckService.checkReceivedMessage(userDetails.getUser());
    }

    @GetMapping("/sent")
    public ResponseDto<?> sentMessage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return messageCheckService.checkSentMessage(userDetails.getUser());
    }

    @GetMapping("/read/{messageId}")
    public ResponseDto<?> readMessage(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long messageId) {
        return messageCheckService.readMessage(userDetails.getUser(), messageId);
    }
}