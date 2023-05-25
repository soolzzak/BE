package com.example.zzan.room.repository;

import com.example.zzan.room.entity.Room;
import com.example.zzan.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

	Optional<User> findByUsername(String username);
}