package com.example.zzan.room.repository;

import com.example.zzan.room.entity.Category;
import com.example.zzan.room.entity.GenderSetting;
import com.example.zzan.room.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
	Page<Room> findAllByTitleContainingAndRoomDeleteIsFalse(String title, Pageable pageable);
	Page<Room> findAllByRoomDeleteIsFalse(Pageable pageable);
    Page<Room> findByCategoryAndGenderSettingAndRoomCapacityLessThan(Category category, Pageable pageable, GenderSetting genderSetting, int i);
	Page<Room> findByCategoryAndGenderSetting(Category category, Pageable pageable, GenderSetting genderSetting);
	Page<Room> findByCategoryAndRoomCapacityLessThan(Category category, Pageable pageable, int i);
	Page<Room> findByCategory(Category category, Pageable pageable);
	Page<Room> findByGenderSettingAndRoomCapacityLessThan(GenderSetting genderSetting, int i, Pageable pageable);
	Page<Room> findByGenderSetting(GenderSetting genderSetting, Pageable pageable);
	Page<Room> findByRoomCapacityLessThan(int i, Pageable pageable);
}