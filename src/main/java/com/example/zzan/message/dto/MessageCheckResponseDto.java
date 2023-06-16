package com.example.zzan.message.dto;

import com.example.zzan.message.entity.Messages;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class MessageCheckResponseDto {
    private Long messageId;
    private String content;
    private Boolean isRead;
    private String senderUsername;
    private String receiverUsername;
    private String createdAt;

    public MessageCheckResponseDto(Messages messages) {
        LocalDateTime time = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = time.format(formatter);
        this.messageId = messages.getMessageId();
        this.content = messages.getContent();
        this.isRead = messages.isRead();
        this.senderUsername = messages.getSendUser().getUsername();
        this.receiverUsername = messages.getReceiveUser().getUsername();
        this.createdAt = formattedNow;
    }
}
