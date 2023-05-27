package com.example.zzan.userHistory.repository;

import com.example.zzan.user.entity.User;
import com.example.zzan.userHistory.entity.UserHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

	@Query("SELECT r FROM UserHistory r WHERE r.hostUser = :user OR r.guestUser = :user ORDER BY r.createdAt DESC")
	List<UserHistory> findTop4ByHostUserOrEnterUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);
}
