package com.example.zzan.message.entity;

import com.example.zzan.global.Timestamped;
import com.example.zzan.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Messages extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "SEND_ID")
    private User sendUser;

    @ManyToOne
    @JoinColumn(name = "RECEIVE_ID")
    private User receiveUser;

    @Column(columnDefinition = "VARCHAR(100)")
    private String content;

    private boolean isRead;

    public Messages(User sendUser, User receiveUser, String content, boolean isRead) {
        this.sendUser = sendUser;
        this.receiveUser = receiveUser;
        this.content = content;
        this.isRead = isRead;
    }

    public void markRead() {
        this.isRead = true;
    }
}
