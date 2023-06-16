package com.example.zzan.message.repository;

import com.example.zzan.message.entity.Messages;
import com.example.zzan.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository <Messages, Long> {
    List<Messages> findAllByReceiveUser(User receiveUser);
    List<Messages> findAllBySendUser(User sendUser);
}
