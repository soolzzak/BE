package com.example.zzan.room.repository;

import com.example.zzan.room.entity.Category;
import com.example.zzan.room.entity.GenderSetting;
import com.example.zzan.room.entity.Room;
import com.example.zzan.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
	Page<Room> findByGenderSettingAndRoomCapacityLessThan(Pageable pageable, GenderSetting genderSetting, int roomCapacity);
	Page<Room> findByRoomCapacityLessThan(Pageable pageable, int roomCapacity);

	Page<Room> findByGenderSetting(Pageable pageable, GenderSetting genderSetting);
	Page<Room> findAllByTitleContainingAndRoomDeleteIsFalse(String title, Pageable pageable);
	Page<Room> findAllByRoomDeleteIsFalse(Pageable pageable);
	Page<Room> findAllByCategoryAndRoomDeleteIsFalse(Category category, Pageable pageable);

}