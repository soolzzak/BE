package com.example.zzan.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.zzan.room.entity.Room;
import com.example.zzan.room.entity.RoomHistory;

public interface RoomHistoryRepository extends JpaRepository<RoomHistory, Long> {


}
