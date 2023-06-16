package com.example.zzan.message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageSendRequestDto {
    private String receiverUsername;
    private String content;
}
